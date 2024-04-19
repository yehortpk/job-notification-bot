package com.github.yehortpk.parser.domain.scrappers;

import com.github.yehortpk.parser.models.PageConnectionParams;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Default pages scrapper. Scrap page with Jsoup library
 */
@Component
public class DefaultPageScrapper implements PageScrapper {
    @Override
    public String scrapPage(PageConnectionParams pageConnectionParams) throws IOException {
        return toConnection(pageConnectionParams).execute().body();
    }

    /**
     * Transform {@link PageConnectionParams} object into Jsoup {@link Connection}
     * @param pageConnectionParams connection parameters of the page
     * @return page connection object
     */
    private Connection toConnection(PageConnectionParams pageConnectionParams) {
        return Jsoup.connect(pageConnectionParams.getPageUrl())
                .userAgent(pageConnectionParams.getUserAgent())
                .proxy(pageConnectionParams.getProxy())
                .headers(pageConnectionParams.getHeaders())
                .data(pageConnectionParams.getData())
                .method(pageConnectionParams.getConnectionMethod());
    }


}
