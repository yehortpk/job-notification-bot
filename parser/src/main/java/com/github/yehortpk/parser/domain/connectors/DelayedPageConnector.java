package com.github.yehortpk.parser.domain.connectors;

import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.domain.scrappers.PageScrapper;
import com.github.yehortpk.parser.models.ScrapperResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Page connector with configured delay between pages parsing. Based on the site pages parsing strategy in threads,
 * delay is necessary for avoid ban for your IP. Delegates {@link IOException} from {@link PageScrapper}
 *
 */
@RequiredArgsConstructor
@Slf4j
public class DelayedPageConnector implements PageConnector {
    private final PageScrapper pageScrapper;

    @SneakyThrows
    @Override
    public ScrapperResponseDTO connectToPage(PageConnectionParams pageConnectionParams) throws IOException {
        Thread.sleep(pageConnectionParams.getDelay());

        log.info("Connect to the page {}, delay:{}ms, data: {}, headers: {}",
                pageConnectionParams.getPageUrl(),
                pageConnectionParams.getDelay(),
                pageConnectionParams.getData(),
                pageConnectionParams.getHeaders());
        ScrapperResponseDTO pageBody = pageScrapper.scrapPage(pageConnectionParams);
        log.info("Connection to the page: {}, data: {} was established",
                pageConnectionParams.getPageUrl(),
                pageConnectionParams.getData());
        return pageBody;
    }
}
