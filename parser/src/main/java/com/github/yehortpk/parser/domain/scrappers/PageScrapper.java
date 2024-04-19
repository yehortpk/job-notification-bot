package com.github.yehortpk.parser.domain.scrappers;

import com.github.yehortpk.parser.domain.connectors.PageConnector;
import com.github.yehortpk.parser.models.PageConnectionParams;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Interface for all page scrappers. Use {@link PageConnector} to acquire connection to the page, and scrapper to
 * scrap page HTML.
 * @see DefaultPageScrapper
 * @see ComponentPageScrapper
 */
@Component
public interface PageScrapper {
    /**
     * Scraps page with {@link PageConnectionParams}
     * @param pageConnectionParams connection parameters of the page
     * @return page body
     * @throws IOException delegates {@link IOException} from {@link PageConnector}
     */
    String scrapPage(PageConnectionParams pageConnectionParams) throws IOException;
}
