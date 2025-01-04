package com.github.yehortpk.parser.domain.parser.page;

import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.models.PageParserResponse;

import java.io.IOException;

/**
 * Interface for all page parsers.
 * scrap page HTML.
 * @see DefaultPageParser
 * @see ComponentPageParser
 */
public interface PageParser {
    /**
     * Scraps page with {@link PageConnectionParams}
     * @param pageConnectionParams connection parameters of the page
     * @return page body
     */
    PageParserResponse parsePage(PageConnectionParams pageConnectionParams) throws IOException;
}
