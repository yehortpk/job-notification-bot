package com.github.yehortpk.notifier.scrappers;

import com.github.yehortpk.notifier.exceptions.PageConnectionException;
import com.github.yehortpk.notifier.models.PageConnectionParams;
import com.github.yehortpk.notifier.services.ProxyService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Setter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Slf4j
public abstract class PageScrapperImpl implements PageScrapper {
    @Getter
    protected final PageConnectionParams pageConnectionParams;
    protected final ProxyService proxyService;
    private final int initialPollTimeout = 60;

    public String loadPage() {
        int initialThreadsCount = proxyService.getProxies().size();
        int initialThreadsTimeout = 50;

        return loadPage(initialThreadsCount, initialPollTimeout, initialThreadsTimeout);
    }

    @SneakyThrows
    private String loadPage(int threadsCount, int pollTimeout, int threadsTimeout) {
        if ((double) pollTimeout /this.initialPollTimeout >= 1.5) {
            return scrapPage();
        }

        List<Future<String>> futures = new ArrayList<>();
        @Cleanup ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

        log.info("Try page {}, data: {}", pageConnectionParams.getPageUrl(), pageConnectionParams.getData());
        for (int i = 0; i < threadsCount; i++) {
            Proxy randomProxy = proxyService.getRandomProxy();
            int currentThreadsTimeout = threadsTimeout * i;
            Callable<String> task = () -> {
                Thread.sleep(currentThreadsTimeout);
                return scrapPage(randomProxy);
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
                        if(!(e.getCause() instanceof PageConnectionException)) {
                            log.error("Page: {}, error: {}", pageConnectionParams.getPageUrl(), e.getCause().getMessage());
                        }
                    }
                }
            } catch (InterruptedException e) {
                log.error("interrupted, error: {}", e.getMessage());
            }
        }

        Thread.sleep(2500);
        return loadPage(threadsCount, pollTimeout + 10, threadsTimeout + 100);
    }

    protected abstract String scrapPage();
    protected abstract String scrapPage(Proxy proxy);
}
