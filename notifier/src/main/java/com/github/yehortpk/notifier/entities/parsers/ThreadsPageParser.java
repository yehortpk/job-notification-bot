package com.github.yehortpk.notifier.entities.parsers;

import com.github.yehortpk.notifier.services.ProxyService;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Setter
@ToString(onlyExplicitlyIncluded = true)
public class ThreadsPageParser implements PageParser {
    private ProxyService proxyService;

    @ToString.Include
    private Map<String, String> headers;
    private int initialPollTimeout = 60;

    @Override
    public Document loadPage(String pageURL) {
        int initialThreadsCount = proxyService.getProxies().size();

        return loadPage(pageURL, initialThreadsCount, initialPollTimeout);
    }

    @SneakyThrows
    private Document loadPage(String pageURL, int threadsCount, int pollTimeout) {
        if ((double) pollTimeout /this.initialPollTimeout >= 1.5) {
            System.out.printf("Can't parse page: %s", pageURL);
            try {
                return parsePageWithoutProxy(pageURL);
            } catch (Exception e) {
                throw new RuntimeException("Can't parse page, error = " + e.getMessage());
            }
        }

        System.out.printf("Try page: %s%n", pageURL);
        List<Future<Document>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CompletionService<Document> completionService = new ExecutorCompletionService<>(executor);

        for (int i = 0; i < threadsCount; i++) {
            Callable<Document> task = parsePage(pageURL);
//            Thread.sleep(100);
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
                        if(!(e.getCause() instanceof SocketTimeoutException)) {
                            System.out.printf("Page: %s, error: %s%n", pageURL, e.getMessage());
                        }
                        continue;
                    }

                    futures.forEach((fut) -> fut.cancel(true));

                    executor.shutdown();
                    return result;

                }
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
        }

        executor.shutdown();
        Thread.sleep(2500);
        return loadPage(pageURL, threadsCount, pollTimeout + 10);
    }

    private Callable<Document> parsePage(String pageUrl) {
        Proxy randomProxy = proxyService.getRandomProxy();
//        System.out.printf("Try: %s, proxy=%s, headers=%s%n", pageUrl, randomProxy, this.headers);
        return () -> {
            Document document = Jsoup.connect(pageUrl)
                    .proxy(randomProxy)
                    .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316" +
                            " Firefox/3.6.2")
                    .headers(this.headers)
                    .get();
            System.out.printf("Page %s parsed with proxy %s%n", pageUrl, randomProxy);
            return document;
        };
    }

    private Document parsePageWithoutProxy(String pageUrl) throws IOException {
        return Jsoup.connect(pageUrl)
                .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316" +
                        " Firefox/3.6.2")
                .get();
    }
}
