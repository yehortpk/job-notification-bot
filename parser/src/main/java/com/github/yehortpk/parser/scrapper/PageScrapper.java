package com.github.yehortpk.parser.scrapper;

import com.github.yehortpk.parser.models.PageConnectionParams;

import java.io.IOException;

/**
 * Interface for all page parsers.
 * scrap page HTML.
 * @see StaticPageScrapper
 * @see DynamicPageScrapper
 */
public interface PageScrapper {
    /**
     * Scraps page with {@link PageConnectionParams}
     * @param pageConnectionParams connection parameters of the page
     * @return page body
     */
    PageScrapperResponse scrapPage(PageConnectionParams pageConnectionParams) throws IOException;
}
