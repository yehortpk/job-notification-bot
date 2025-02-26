package com.github.yehortpk.parser.scrapper.page.decorator;

import com.github.yehortpk.parser.scrapper.page.PageScrapper;

/**
 * Interface for all page parser decorators. These wrappers will enhance default
 * <code>parsePage</code> method behaviour
 * scrap page HTML.
 * @see ProxyPoolPageScrapperDecorator
 */
public interface PageScrapperDecorator extends PageScrapper {
}
