package com.github.yehortpk.notifier.domain.connectors;

import com.github.yehortpk.notifier.exceptions.ProxyPageConnectionException;
import com.github.yehortpk.notifier.models.PageConnectionParams;
import com.github.yehortpk.notifier.domain.scrappers.PageScrapper;
import com.github.yehortpk.notifier.services.ProxyService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Slf4j
public class ProxyPageConnector implements PageConnector {
    @NonNull private final PageScrapper pageScrapper;
    @NonNull private final ProxyService proxyService;
    private final int INITIAL_POLL_TIMEOUT = 60;
    private final int INITIAL_THREAD_TIMEOUT = 50;
    private final int POLL_TIMEOUT_INC = 10;
    private final int THREAD_TIMEOUT_INC = 100;


    @Override
    public String connectToPage(PageConnectionParams pageConnectionParams) throws IOException {

        String pageBody = loadPage(INITIAL_POLL_TIMEOUT, INITIAL_THREAD_TIMEOUT, pageConnectionParams);
        log.info("Connection to the page: {}, data: {}, proxy: {} was established",
                pageConnectionParams.getPageUrl(),
                pageConnectionParams.getData(),
                pageConnectionParams.getProxy());
        return pageBody;
    }

    private String loadPage(int pollTimeout, int threadsTimeout,
                            PageConnectionParams pageConnectionParams) throws IOException {
        if((pollTimeout - INITIAL_POLL_TIMEOUT) > POLL_TIMEOUT_INC * 2) {
            throw new IOException();
        }

        List<Future<String>> futures = new ArrayList<>();
        @Cleanup ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

        log.info("Connect to the page {}, data: {}, headers: {}", pageConnectionParams.getPageUrl(),
                pageConnectionParams.getData(), pageConnectionParams.getHeaders());
        AtomicInteger timeoutCounter = new AtomicInteger();
        for (int counter = 0; counter < proxyService.getProxies().size(); counter++) {
            Proxy randomProxy = proxyService.getRandomProxy();
            Callable<String> task = () -> {
                if (proxyService.validateProxy(randomProxy)) {
                    int currentThreadsTimeout = threadsTimeout * timeoutCounter.getAndIncrement();
                    Thread.sleep(currentThreadsTimeout);
                    pageConnectionParams.setProxy(randomProxy);
                    return pageScrapper.scrapPage(pageConnectionParams);
                }
                throw new ProxyPageConnectionException("Proxy can't load the page");
            };
            Future<String> future = completionService.submit(task);
            futures.add(future);
        }

        for (int i = 0; i < futures.size(); i++) {
            try {
                Future<String> completedFuture = completionService.poll(pollTimeout, TimeUnit.SECONDS);

                if (completedFuture != null) {
                    try {
                        String result = completedFuture.get();
                        futures.forEach((fut) -> fut.cancel(true));

                        executor.close();
                        return result;
                    } catch (ExecutionException e) {
                        if(!(e.getCause() instanceof TimeoutException ||
                            e.getCause() instanceof ProxyPageConnectionException)) {
                            log.error("Page: {}, proxy: {}, error: {}",
                                    pageConnectionParams.getPageUrl(),
                                    pageConnectionParams.getProxy(),
                                    e.getCause().getMessage());
                        }
                    }
                }
            } catch (InterruptedException e) {
                log.error("interrupted, error: {}", e.getMessage());
            }
        }

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            log.error("interrupted, error: {}", e.getMessage());
        }

        log.error("Could not connect to the page {} for specified timeout. Retry", pageConnectionParams.getPageUrl());
        return loadPage(pollTimeout + POLL_TIMEOUT_INC, threadsTimeout + THREAD_TIMEOUT_INC, pageConnectionParams);
    }
}