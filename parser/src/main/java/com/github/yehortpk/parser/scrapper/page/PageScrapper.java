package com.github.yehortpk.parser.scrapper.page;

import com.github.yehortpk.parser.models.PageRequestParams;

import java.io.IOException;

/**
 * Interface for all page parsers.
 * scrap page HTML.
 * @see StaticPageScrapper
 * @see DynamicPageScrapper
 */
public interface PageScrapper {
    /**
     * Scraps page with {@link PageRequestParams}
     * @param pageRequestParams connection parameters of the page
     * @return page body
     */
    PageScrapperResponse scrapPage(PageRequestParams pageRequestParams) throws IOException;
}
