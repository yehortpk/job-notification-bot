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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Page connector based on proxy strategy. Multiple threads have its own proxy, and trying to scrap the same page. Each
 * thread has page poll timeout, when it returns {@link TimeoutException} after the expiration of time.
 * Every proxy thread have its own delay based on its sequence number (by default - 50ms). When the proxy is in the
 * delay it may be cancelled by another thread that complete the page scrapping.
 */
@RequiredArgsConstructor
@Slf4j
public class ProxyPageConnector implements PageConnector {
    private final PageScrapper pageScrapper;
    private final ProxyService proxyService;

    private final int INITIAL_DELAY_BETWEEN_THREADS = 50;
    private final int TIMEOUT_SECONDS = 30;


    @Override
    public ScrapperResponseDTO connectToPage(PageConnectionParams pageConnectionParams) throws IOException {

        ScrapperResponseDTO pageBody = loadPage(pageConnectionParams);
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
    private ScrapperResponseDTO loadPage(PageConnectionParams pageConnectionParams) throws IOException {

        Map<Future<ScrapperResponseDTO>, Proxy> pageResponseWithProxiesFut = new HashMap<>();
        @Cleanup ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CompletionService<ScrapperResponseDTO> completionService = new ExecutorCompletionService<>(executor);


        proxyService.filterValidProxies();
        List<Proxy> proxies = proxyService.getProxies();
        for (int counter = 0; counter < proxies.size(); counter++) {
            Proxy proxy = proxies.get(counter);
            int currentThreadsTimeout = INITIAL_DELAY_BETWEEN_THREADS * counter;
            Callable<ScrapperResponseDTO> task = () -> {
                Thread.sleep(currentThreadsTimeout);
                return pageScrapper.scrapPage(pageConnectionParams);
            };
            Future<ScrapperResponseDTO> pageResponseWithProxy = completionService.submit(task);
            log.info("Connect to the page {}, proxy: {},  data: {}, headers: {}",
                    pageConnectionParams.getPageUrl(),
                    proxy,
                    pageConnectionParams.getData(),
                    pageConnectionParams.getHeaders()
            );
            pageResponseWithProxiesFut.put(pageResponseWithProxy, proxy);
        }

        for (int i = 0; i < proxies.size(); i++) {
            Future<ScrapperResponseDTO> scrapperResponseFut;
            try {
                scrapperResponseFut = completionService.take();
            } catch (InterruptedException e) {
                throw new RuntimeException("Service was interrupted");
            }
            try {
                ScrapperResponseDTO result = scrapperResponseFut.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                log.info("Connection to the page: {}, proxy: {}, data: {} was established",
                        pageConnectionParams.getPageUrl(),
                        pageResponseWithProxiesFut.get(scrapperResponseFut),
                        pageConnectionParams.getData());
                pageResponseWithProxiesFut.forEach((res, proxy) -> res.cancel(true));

                executor.close();
                return result;
            } catch (InterruptedException | ExecutionException | java.util.concurrent.TimeoutException ee) {
                if (ee.getCause() instanceof ProxyPageConnectionException) {
                    log.error("Page: {}, proxy: {}, error: {}",
                            pageConnectionParams.getPageUrl(),
                            pageResponseWithProxiesFut.get(scrapperResponseFut),
                            ee.getCause().getMessage());
                } else {
                    log.error("Page: {}, error: {}",
                            pageConnectionParams.getPageUrl(),
                            ee.getCause().getMessage());
                }
            }
        }

        throw new IOException(String.format("Could not connect to the page %s for specified timeout", pageConnectionParams.getPageUrl()));
    }
}