package com.github.yehortpk.parser.scrapper.decorator;

import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.scrapper.PageScrapper;
import com.github.yehortpk.parser.scrapper.PageScrapperResponse;
import com.github.yehortpk.parser.proxy.ProxyService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Page wrapper based on proxy strategy. Multiple threads have its own proxy, and trying to parse the same page. Each
 * thread has page poll timeout, when it returns {@link TimeoutException} after the expiration of time.
 * Every proxy thread have its own delay based on its sequence number (by default - 50ms). When the proxy is in the
 * delay it may be cancelled by another thread that complete the page scrapping.
 */
@Slf4j
@RequiredArgsConstructor
public class ProxyPoolPageScrapperDecorator implements PageScrapperDecorator {
    private final PageScrapper pageScrapper;
    private final ProxyService proxyService = ProxyService.getInstance();

    private int MAX_PARALLEL_INSTANCES_COUNT = 5;
    private final int INITIAL_DELAY_BETWEEN_THREADS_MS = 500;


    @Override
    public PageScrapperResponse scrapPage(PageConnectionParams pageConnectionParams) throws IOException {
        PageScrapperResponse pageBody = loadPage(pageConnectionParams);
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
     * @param pageConnectionParams connection parameters for the page
     * @return page HTML
     * @see PageConnectionParams
     */
    private PageScrapperResponse loadPage(PageConnectionParams pageConnectionParams) throws IOException {

        Map<Future<PageScrapperResponse>, Proxy> pageResponseWithProxiesFut = new HashMap<>();
        @Cleanup ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CompletionService<PageScrapperResponse> completionService = new ExecutorCompletionService<>(executor);

        List<Proxy> proxies = proxyService.getProxyPool();
        MAX_PARALLEL_INSTANCES_COUNT = Math.min(MAX_PARALLEL_INSTANCES_COUNT, proxies.size());
        for (int counter = 0; counter < MAX_PARALLEL_INSTANCES_COUNT; counter++) {
            Proxy proxy = proxyService.getRandomProxy();
            PageConnectionParams pageConnectionParamsClone = SerializationUtils.clone(pageConnectionParams);
            pageConnectionParamsClone.setProxy(proxy);

            int currentThreadsTimeout = INITIAL_DELAY_BETWEEN_THREADS_MS * counter;
            Callable<PageScrapperResponse> task = () -> {
                Thread.sleep(currentThreadsTimeout);
                return pageScrapper.scrapPage(pageConnectionParamsClone);
            };
            Future<PageScrapperResponse> pageResponseWithProxy = completionService.submit(task);
            pageResponseWithProxiesFut.put(pageResponseWithProxy, proxy);
        }


        try {
            int remainingFutures = pageResponseWithProxiesFut.size();
            long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(pageConnectionParams.getTimeoutSec())
                    + (long) INITIAL_DELAY_BETWEEN_THREADS_MS * remainingFutures;

            while (remainingFutures > 0) {
                long timeoutLeft = deadline - System.currentTimeMillis();
                if (timeoutLeft <= 0) {
                    break;
                }

                Future<PageScrapperResponse> completedFuture =
                        completionService.poll(timeoutLeft, TimeUnit.MILLISECONDS);

                if (completedFuture == null) {
                    break;
                }

                remainingFutures--;

                try {
                    PageScrapperResponse response = completedFuture.get();

                    cancelRemainingFutures(pageResponseWithProxiesFut.keySet());
                    return response;

                } catch (InterruptedException | ExecutionException e) {
                    log.error("Page: {}, proxy: {}, error: {}",
                            pageConnectionParams.getPageUrl(),
                            pageResponseWithProxiesFut.get(completedFuture),
                            e.getCause().getMessage());
                }
            }

            // Clean up if we exit the loop without finding a successful response
            cancelRemainingFutures(pageResponseWithProxiesFut.keySet());

            throw new IOException(String.format("Could not connect to the page %s within %d seconds",
                    pageConnectionParams.getPageUrl(), pageConnectionParams.getTimeoutSec()));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            cancelRemainingFutures(pageResponseWithProxiesFut.keySet());
            throw new IOException("Thread was interrupted while waiting for page parsing", e);
        }
    }

    private void cancelRemainingFutures(Set<Future<PageScrapperResponse>> futures) {
        for (Future<PageScrapperResponse> future : futures) {
            if (!future.isDone() && !future.isCancelled()) {
                future.cancel(true);
            }
        }
    }
}