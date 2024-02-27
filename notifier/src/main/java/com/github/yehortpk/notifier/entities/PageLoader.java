package com.github.yehortpk.notifier.entities;

import com.github.yehortpk.notifier.entities.parsers.PageParserImpl;
import com.github.yehortpk.notifier.services.ProxyService;
import lombok.*;
import org.jsoup.nodes.Document;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Setter
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class PageLoader {
    private ProxyService proxyService;
    private PageParserImpl pageParser;
    private final int initialPollTimeout = 60;

    public Document loadPage() {
        int initialThreadsCount = proxyService.getProxies().size();
        int initialThreadsTimeout = 50;

        return loadPage(initialThreadsCount, initialPollTimeout, initialThreadsTimeout);
    }

    @SneakyThrows
    private Document loadPage(int threadsCount, int pollTimeout, int threadsTimeout) {
        if ((double) pollTimeout /this.initialPollTimeout >= 1.5) {
            try {
                return pageParser.parsePage();
            } catch (Exception e) {
                throw new RuntimeException(String.format("Can't parse page %s, error: %s",
                        pageParser.getPageUrl(), e.getMessage()));
            }
        }

        System.out.printf("Try page: %s, page_id: %s%n",
                pageParser.getPageUrl().formatted(pageParser.getPageId()), pageParser.getPageId());
        List<Future<Document>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CompletionService<Document> completionService = new ExecutorCompletionService<>(executor);

        for (int i = 0; i < threadsCount; i++) {
            Proxy randomProxy = proxyService.getRandomProxy();
            int currentThreadsTimeout = threadsTimeout * i;
            Callable<Document> task = () -> {
                Thread.sleep(currentThreadsTimeout);
                return pageParser.parsePage(randomProxy);
            };
            Future<Document> future = completionService.submit(task);
            futures.add(future);
        }

        for (int i = 0; i < futures.size(); i++) {
            try {
                Future<Document> completedFuture = completionService.poll(pollTimeout, TimeUnit.SECONDS);

                if (completedFuture != null) {
                    Document result;
                    try {
                        result = completedFuture.get();
                    } catch (ExecutionException e) {
//                        if(!(e.getCause() instanceof SocketTimeoutException)) {
//                            System.out.printf("Page: %s, error: %s%n", pageParser.getPageUrl(), e.getMessage());
//                        }
                        continue;
                    }

                    futures.forEach((fut) -> fut.cancel(true));

                    executor.close();
                    return result;

                }
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
        }

        executor.shutdown();
        Thread.sleep(2500);
        return loadPage(threadsCount, pollTimeout + 10, threadsTimeout + 100);
    }
}
