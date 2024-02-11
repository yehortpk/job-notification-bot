package com.github.yehortpk.notifier.entities.parsers;

import com.github.yehortpk.notifier.services.ProxyService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadsPageParser implements PageParser{
    ProxyService proxyService = ProxyService.getInstance();

    @Override
    public Document loadPage(String pageURL) {
        int initialThreadsCount = 30;

        return loadPage(pageURL, initialThreadsCount);
    }

    private Document loadPage(String pageURL, int threadsCount) {
        List<Future<Document>> futures = new ArrayList<>();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadsCount);
        CompletionService<Document> completionService = new ExecutorCompletionService<>(executor);

        for (int i = 0; i < threadsCount; i++) {
            Callable<Document> task = parsePage(pageURL);
            Future<Document> future = completionService.submit(task);
            futures.add(future);
        }

        int pollTimeout = 30;
        for (int i = 0; i < futures.size(); i++) {
            try {
                Future<Document> completedFuture = completionService.poll(pollTimeout, TimeUnit.SECONDS);

                if (completedFuture != null) {
                    Document result;
                    try {
                        result = completedFuture.get();
                    } catch (ExecutionException e) {
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
        return loadPage(pageURL, threadsCount + 10);
    }

    private Callable<Document> parsePage(String pageUrl) {
        Proxy randomProxy = proxyService.getRandomProxy();
        return () -> Jsoup.connect(pageUrl)
                .proxy(randomProxy)
                .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316" +
                        " Firefox/3.6.2")
                .header("Content-Language", "en-US")
                .get();
    }
}
