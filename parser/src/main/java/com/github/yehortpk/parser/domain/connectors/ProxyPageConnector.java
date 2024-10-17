package com.github.yehortpk.parser.domain.connectors;

import com.github.yehortpk.parser.exceptions.ProxyPageConnectionException;
import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.domain.scrappers.PageScrapper;
import com.github.yehortpk.parser.models.ScrapperResponseDTO;
import com.github.yehortpk.parser.services.ProxyService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Page connector based on proxy strategy. Multiple threads have its own proxy, and trying to scrap the same page. Each
 * thread has page poll timeout, when it returns {@link TimeoutException} after the expiration of time.
 * Every proxy thread have its own delay based on its sequence number (by default - 50ms). When the proxy is in the
 * delay it may be cancelled by another thread that complete the page scrapping.
 */
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Slf4j
public class ProxyPageConnector implements PageConnector {
    @NonNull private final PageScrapper pageScrapper;
    @NonNull private final ProxyService proxyService;

    private final int INITIAL_POLL_TIMEOUT = 60;
    private final int INITIAL_DELAY_BETWEEN_THREADS = 50;
    private final int POLL_TIMEOUT_LAMBDA = 10;
    private final int DELAY_BETWEEN_THREADS_LAMBDA = 100;
    private final int CONNECTION_MAX_ATTEMPTS = 3;


    @Override
    public ScrapperResponseDTO connectToPage(PageConnectionParams pageConnectionParams) throws IOException {

        ScrapperResponseDTO pageBody = loadPage(INITIAL_POLL_TIMEOUT, INITIAL_DELAY_BETWEEN_THREADS, pageConnectionParams);
        log.info("Connection to the page: {}, data: {}, proxy: {} was established",
                pageConnectionParams.getPageUrl(),
                pageConnectionParams.getData(),
                pageConnectionParams.getProxy());
        return pageBody;
    }

    /**
     * Recursive method that responsible for scrap the page with {@link PageScrapper}. If all the threads fail page
     * connection, method call itself recursively with increased pollTimeout (POLL_TIMEOUT_LAMBDA) and increased
     * delay between threads (DELAY_BETWEEN_THREADS_LAMBDA). When the number of attempts exceeds
     * CONNECTION_MAX_ATTEMPTS, {@link NullPointerException} will be thrown
     *
     * @param pollTimeout timeout for every thread to poll the page.
     * @param threadsDelay delay between threads connection attempts
     * @param pageConnectionParams connection parameters for the page
     * @return page HTML
     * @throws IOException when the number of attempts exceeds CONNECTION_MAX_ATTEMPTS
     * @see PageConnectionParams
     */
    private ScrapperResponseDTO loadPage(int pollTimeout, int threadsDelay,
                            PageConnectionParams pageConnectionParams) throws IOException {
        if((pollTimeout - INITIAL_POLL_TIMEOUT) >= POLL_TIMEOUT_LAMBDA * CONNECTION_MAX_ATTEMPTS) {
            throw new IOException();
        }

        List<Future<ScrapperResponseDTO>> futures = new ArrayList<>();
        @Cleanup ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CompletionService<ScrapperResponseDTO> completionService = new ExecutorCompletionService<>(executor);

        log.info("Connect to the page {}, data: {}, headers: {}", pageConnectionParams.getPageUrl(),
                pageConnectionParams.getData(), pageConnectionParams.getHeaders());
        AtomicInteger timeoutCounter = new AtomicInteger();
        for (int counter = 0; counter < proxyService.getProxies().size(); counter++) {
            Proxy randomProxy = proxyService.getRandomProxy();
            Callable<ScrapperResponseDTO> task = () -> {
                if (proxyService.validateProxy(randomProxy)) {
                    int currentThreadsTimeout = threadsDelay * timeoutCounter.getAndIncrement();
                    Thread.sleep(currentThreadsTimeout);
                    pageConnectionParams.setProxy(randomProxy);
                    return pageScrapper.scrapPage(pageConnectionParams);
                }
                throw new ProxyPageConnectionException("Proxy can't load the page");
            };
            Future<ScrapperResponseDTO> future = completionService.submit(task);
            futures.add(future);
        }

        for (int i = 0; i < futures.size(); i++) {
            try {
                Future<ScrapperResponseDTO> completedFuture = completionService.poll(pollTimeout, TimeUnit.SECONDS);

                if (completedFuture != null) {
                    try {
                        ScrapperResponseDTO result = completedFuture.get();
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
        return loadPage(pollTimeout + POLL_TIMEOUT_LAMBDA, threadsDelay + DELAY_BETWEEN_THREADS_LAMBDA, pageConnectionParams);
    }
}