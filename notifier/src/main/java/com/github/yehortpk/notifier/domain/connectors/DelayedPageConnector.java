package com.github.yehortpk.notifier.domain.connectors;

import com.github.yehortpk.notifier.models.PageConnectionParams;
import com.github.yehortpk.notifier.domain.scrappers.PageScrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class DelayedPageConnector implements PageConnector {
    private final PageScrapper pageScrapper;

    @Override
    public String connectToPage(PageConnectionParams pageConnectionParams) throws IOException {
        try {
            Thread.sleep(pageConnectionParams.getDelay());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Connect to the page {}, delay:{}ms, data: {}, headers: {}",
                pageConnectionParams.getPageUrl(),
                pageConnectionParams.getDelay(),
                pageConnectionParams.getData(),
                pageConnectionParams.getHeaders());
        String pageBody = pageScrapper.scrapPage(pageConnectionParams);
        log.info("Connection to the page: {}, data: {} was established",
                pageConnectionParams.getPageUrl(),
                pageConnectionParams.getData());
        return pageBody;
    }
}
