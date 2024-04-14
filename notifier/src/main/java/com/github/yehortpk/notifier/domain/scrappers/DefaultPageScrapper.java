package com.github.yehortpk.notifier.domain.scrappers;

import com.github.yehortpk.notifier.models.PageConnectionParams;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DefaultPageScrapper implements PageScrapper {
    @Override
    public String scrapPage(PageConnectionParams pageConnectionParams) throws IOException {
        return toConnection(pageConnectionParams).execute().body();
    }

    private Connection toConnection(PageConnectionParams pageConnectionParams) {
        return Jsoup.connect(pageConnectionParams.getPageUrl())
                .userAgent(pageConnectionParams.getUserAgent())
                .proxy(pageConnectionParams.getProxy())
                .headers(pageConnectionParams.getHeaders())
                .data(pageConnectionParams.getData())
                .method(pageConnectionParams.getConnectionMethod());
    }


}
