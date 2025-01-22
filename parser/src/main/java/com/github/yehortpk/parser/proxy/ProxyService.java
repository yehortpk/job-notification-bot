package com.github.yehortpk.parser.proxy;

import lombok.Cleanup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * This class creates and manages pool of proxies
 */
@Slf4j
public class ProxyService {
    private static final ProxyService instance = new ProxyService();
    @Getter
    private List<Proxy> proxyPool = new ArrayList<>();
    private LocalDateTime createdTime;
    private final int PROXY_POOL_REFRESH_CD_MIN = 10;
    private final int PROXY_VALIDATION_TIMEOUT_SEC = 30;

    private ProxyService() {
        proxyPool = createProxyPool();
    }

    public static ProxyService getInstance() {
        if (instance.isProxyPoolOutdated()) {
            instance.refreshProxies();
        }

        return instance;
    }

    private boolean isProxyPoolOutdated() {
        return createdTime == null
                || createdTime.plusMinutes(PROXY_POOL_REFRESH_CD_MIN).isBefore(LocalDateTime.now());
    }

    private void refreshProxies() {
        proxyPool = createProxyPool();
    }

    public synchronized List<Proxy> createProxyPool() {
        List<Proxy> proxies = new ArrayList<>();
        proxies.addAll(new FreeProxyListProxyParser().parseProxies());
        proxies.addAll(new ProxyScrapeProxyParser().parseProxies());
        log.info("Total Proxies count: {}", proxies.size());

        log.info("Validate proxies... This may take up to 30 sec");

        @Cleanup ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<Proxy>> proxiesFut = new ArrayList<>();

        for (Proxy proxy : proxies) {
            proxiesFut.add(executor.submit(() -> isProxyValid(proxy) ? proxy: null));
        }

        executor.shutdown();
        try {
            // Wait for all tasks to finish with a timeout
            if (!executor.awaitTermination(PROXY_VALIDATION_TIMEOUT_SEC, TimeUnit.SECONDS)) {
                log.warn("Executor did not finish in time, shutting down now.");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Collect the results of completed tasks
        proxies = proxiesFut.stream()
                .map(future -> {
                    try {
                        return future.get(PROXY_VALIDATION_TIMEOUT_SEC, TimeUnit.SECONDS); // Use timeout for get()
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        // Log or handle the exception based on your needs
                        return null; // Return null if there was an error or timeout
                    }
                })
                .filter(Objects::nonNull) // Remove null results (failed tasks)
                .toList();

        log.info("Validated proxies({}): {}", proxies.size(), proxies);

        createdTime = LocalDateTime.now();

        return proxies;
    }

    /**
     * Returns random proxy from the proxy pool
     * @return random proxy
     */
    public Proxy getRandomProxy(){
        int randomIndex = ThreadLocalRandom.current().nextInt(0, proxyPool.size());
        return proxyPool.get(randomIndex);
    }

    /**
     * Validates if proxy is working
     * @param proxy proxy for validation
     * @return is proxy valid
     */
    public boolean isProxyValid(Proxy proxy) {
        final String TEST_ENDPOINT_URL = "https://postman-echo.com/get";
        final int PROXY_TEST_TIMEOUT_MS = 30000; // Timeout in milliseconds

        try {
            if (Thread.currentThread().isInterrupted()) {
                return false;
            }

            URL url = new URI(TEST_ENDPOINT_URL).toURL();
            URLConnection connection = url.openConnection(proxy);
            connection.setConnectTimeout(PROXY_TEST_TIMEOUT_MS);
            connection.setReadTimeout(PROXY_TEST_TIMEOUT_MS); // Set read timeout as well
            connection.connect();

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
