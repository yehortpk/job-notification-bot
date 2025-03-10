package com.github.yehortpk.parser.scrapper.page;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yehortpk.parser.models.PageRequestParams;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Default pages scrapper. Scrap page with Jsoup library
 */
@Component
@Slf4j
public class StaticPageScrapper implements PageScrapper {
    @Override
    public PageScrapperResponse scrapPage(PageRequestParams pageRequestParams) throws IOException {
        log.info("Connecting to the page {}, proxy: {},  data: {}, headers: {}",
                pageRequestParams.getPageURL(),
                pageRequestParams.getProxy(),
                pageRequestParams.getData(),
                pageRequestParams.getHeaders()
        );
        Connection.Response response = toConnection(pageRequestParams).execute();
        log.info("Connection to the page {}, proxy: {},  data: {}, headers: {} was established",
                pageRequestParams.getPageURL(),
                pageRequestParams.getProxy(),
                pageRequestParams.getData(),
                pageRequestParams.getHeaders()
        );
        return new PageScrapperResponse(response.headers(), response.cookies(), response.body());
    }

    /**
     * Transform {@link PageRequestParams} object into Jsoup {@link Connection}
     * @param pageRequestParams connection parameters of the page
     * @return page connection object
     */
    private Connection toConnection(PageRequestParams pageRequestParams) {
        Connection connection = Jsoup.connect(pageRequestParams.getPageURL())
                .proxy(pageRequestParams.getProxy())
                .headers(pageRequestParams.getHeaders())
                .ignoreContentType(true)
                .method(pageRequestParams.getConnectionMethod())
                .timeout(pageRequestParams.getTimeoutSec() * 1000)
                .data(pageRequestParams.getData());

        if (pageRequestParams.getConnectionMethod() == Connection.Method.POST &&
                pageRequestParams.getRequestBody() != null) {
            connection.requestBody(pageRequestParams.getRequestBody());
        }

        String userAgent = pageRequestParams.getHeaders().get("User-Agent");
        if (userAgent != null) {
            connection.userAgent(userAgent);
        }
        return connection;
    }


}
