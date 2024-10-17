package com.github.yehortpk.parser.domain.connectors;

import com.github.yehortpk.parser.domain.scrappers.PageScrapper;
import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.models.ScrapperResponseDTO;

import java.io.IOException;

/**
 * Interface for all connectors. Responsible for provide connection params to the {@link PageScrapper} and return
 * the page HTML
 * @see DelayedPageConnector
 * @see ProxyPageConnector
 */
public interface PageConnector {
    ScrapperResponseDTO connectToPage(PageConnectionParams pageConnectionParams) throws IOException;
}
