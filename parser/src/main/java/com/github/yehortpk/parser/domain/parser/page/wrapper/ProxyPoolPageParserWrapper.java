package com.github.yehortpk.parser.domain.parser.page.wrapper;

import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.domain.parser.page.PageParser;
import com.github.yehortpk.parser.models.PageParserResponse;
import com.github.yehortpk.parser.services.proxy.ProxyService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Page wrapper based on proxy strategy. Multiple threads have its own proxy, and trying to parse the same page. Each
 * thread has page poll timeout, when it returns {@link TimeoutException} after the expiration of time.
 * Every proxy thread have its own delay based on its sequence number (by default - 50ms). When the proxy is in the
 * delay it may be cancelled by another thread that complete the page scrapping.
 */
@Slf4j
@RequiredArgsConstructor
public class ProxyPoolPageParserWrapper implements PageParserWrapper {
    private final PageParser pageParser;
    private final ProxyService proxyService = ProxyService.getInstance();

    private int MAX_PARALLEL_INSTANCES_COUNT = 10;
    private final int INITIAL_DELAY_BETWEEN_THREADS_MS = 500;
    private final int POLL_TIMEOUT_SEC = 30;


    @Override
    public PageParserResponse parsePage(PageConnectionParams pageConnectionParams) throws IOException {
        PageParserResponse pageBody = loadPage(pageConnectionParams);
        log.info("Connection to the page: {}, data: {}, proxy: {} was established",
                pageConnectionParams.getPageUrl(),
                pageConnectionParams.getData(),
                pageConnectionParams.getProxy());
        return pageBody;
    }

    /**
     * Recursive method that responsible for scrap the page with {@link PageParser}. If all the threads fail page
     * connection, method call itself recursively with increased pollTimeout (POLL_TIMEOUT_LAMBDA) and increased
     * delay between threads (DELAY_BETWEEN_THREADS_LAMBDA). When the number of attempts exceeds
     * CONNECTION_MAX_ATTEMPTS, {@link NullPointerException} will be thrown
     *
     * @param pageConnectionParams connection parameters for the page
     * @return page HTML
     * @see PageConnectionParams
     */
    private PageParserResponse loadPage(PageConnectionParams pageConnectionParams) throws IOException {

        Map<Future<PageParserResponse>, Proxy> pageResponseWithProxiesFut = new HashMap<>();
        @Cleanup ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CompletionService<PageParserResponse> completionService = new ExecutorCompletionService<>(executor);

        List<Proxy> proxies = proxyService.getProxyPool();
        MAX_PARALLEL_INSTANCES_COUNT = Math.min(MAX_PARALLEL_INSTANCES_COUNT, proxies.size());
        for (int counter = 0; counter < MAX_PARALLEL_INSTANCES_COUNT; counter++) {
            Proxy proxy = proxyService.getRandomProxy();
            PageConnectionParams pageConnectionParamsClone = SerializationUtils.clone(pageConnectionParams);
            pageConnectionParamsClone.setProxy(proxy);

            int currentThreadsTimeout = INITIAL_DELAY_BETWEEN_THREADS_MS * counter;
            Callable<PageParserResponse> task = () -> {
                Thread.sleep(currentThreadsTimeout);
                return pageParser.parsePage(pageConnectionParamsClone);
            };
            Future<PageParserResponse> pageResponseWithProxy = completionService.submit(task);
            pageResponseWithProxiesFut.put(pageResponseWithProxy, proxy);
        }


        try {
            Future<PageParserResponse> completedFuture = completionService.poll(POLL_TIMEOUT_SEC * 1000 +
                    (long) pageResponseWithProxiesFut.size() * INITIAL_DELAY_BETWEEN_THREADS_MS, TimeUnit.MILLISECONDS);

            if (completedFuture != null) {
                try {
                    PageParserResponse response = completedFuture.get();

                    // Cancel all other threads
                    for (Future<PageParserResponse> future : pageResponseWithProxiesFut.keySet()) {
                        if (!future.isDone() && !future.isCancelled()) {
                            future.cancel(true);
                        }
                    }

                    return response;
                } catch (ExecutionException e) {
                    log.error("Page: {}, proxy: {}, error: {}",
                            pageConnectionParams.getPageUrl(),
                            pageResponseWithProxiesFut.get(completedFuture),
                            e.getCause().getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Thread was interrupted while waiting for page parsing", e);
        }

        throw new IOException(String.format("Could not connect to the page %s within %d seconds",
                pageConnectionParams.getPageUrl(), POLL_TIMEOUT_SEC));}
}