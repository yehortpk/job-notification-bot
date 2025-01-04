package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.services.proxy.FreeProxyListProxyParser;
import jakarta.annotation.PostConstruct;
import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class creates and manages pool of proxies
 */
@Component
@Getter
@Slf4j
@ConditionalOnProperty(
        name = "parser.mode",
        havingValue = "proxy"
)
public class ProxyService {
    private List<Proxy> proxies = new ArrayList<>();
    private static final ThreadLocal<List<Integer>> threadLocalParam = ThreadLocal.withInitial(ArrayList::new);
    private boolean isValidated = false;

    /**
     * Create and filter proxy pool of free proxies list site
     */
    @PostConstruct
    private void createProxyPool(){
        proxies.addAll(new FreeProxyListProxyParser().parseProxies());

        log.info("Total Proxies count: {}", proxies.size());
        resetRange();
    }

    /**
     * Returns new local thread range
     * @return new range of proxies list size
     */
    private List<Integer> resetRange() {
        List<Integer> rangeList = IntStream.range(0, proxies.size())
                .boxed()
                .collect(Collectors.toList());
        threadLocalParam.set(rangeList);
        return threadLocalParam.get();
    }

    public synchronized void filterValidProxies() {
        if (!isValidated) {
            log.info("Validate proxies... This may take up to 30 sec");

            @Cleanup ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            final int EXECUTOR_SERVICE_TIMEOUT = 30;

            proxies = proxies.stream()
                    .map(proxy -> CompletableFuture.supplyAsync(() -> isProxyValid(proxy), executor)
                            .thenApply(isValid -> isValid ? proxy : null)
                            .orTimeout(EXECUTOR_SERVICE_TIMEOUT, TimeUnit.SECONDS)
                            .exceptionally(ex -> null))
                    .toList()
                    .stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .toList();

            executor.shutdown();
            try {
                if (!executor.awaitTermination(EXECUTOR_SERVICE_TIMEOUT, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }

            log.info("Validated proxies({}): {}", proxies.size(), proxies);

            isValidated = true;
        }
    }

    /**
     * Returns random proxy from the proxy pool
     * @return random proxy
     */
    public Proxy getRandomProxy(){
        List<Integer> range = threadLocalParam.get();
        if(range.isEmpty()) {
            range = resetRange();
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(0, range.size());
        int randomNum = range.remove(randomIndex);
        threadLocalParam.set(range);

        return proxies.get(randomNum);
    }

    /**
     * Validates if proxy is working
     * @param proxy proxy for validation
     * @return is proxy valid
     */
    @SneakyThrows
    public boolean isProxyValid(Proxy proxy) {
        final String GOOGLE_URL = "https://www.google.com";
        try {
            URL url = new URI(GOOGLE_URL).toURL();
            URLConnection connection = url.openConnection(proxy);

            connection.setConnectTimeout(5000);

            connection.connect();

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
