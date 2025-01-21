package com.github.yehortpk.parser.domain.parser.page;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.models.PageParserResponse;
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
public class DefaultPageParser implements PageParser {
    @Override
    public PageParserResponse parsePage(PageConnectionParams pageConnectionParams) throws IOException {
        log.info("Connect to the page {}, proxy: {},  data: {}, headers: {}",
                pageConnectionParams.getPageUrl(),
                pageConnectionParams.getProxy(),
                pageConnectionParams.getData(),
                pageConnectionParams.getHeaders()
        );
        Connection.Response response = toConnection(pageConnectionParams).execute();
        log.info("Connection to the page {}, proxy: {},  data: {}, headers: {} was established",
                pageConnectionParams.getPageUrl(),
                pageConnectionParams.getProxy(),
                pageConnectionParams.getData(),
                pageConnectionParams.getHeaders()
        );
        return new PageParserResponse(response.headers(), response.cookies(), response.body());
    }

    /**
     * Transform {@link PageConnectionParams} object into Jsoup {@link Connection}
     * @param pageConnectionParams connection parameters of the page
     * @return page connection object
     */
    private Connection toConnection(PageConnectionParams pageConnectionParams) {
        Connection connection = Jsoup.connect(pageConnectionParams.getPageUrl())
                .proxy(pageConnectionParams.getProxy())
                .headers(pageConnectionParams.getHeaders())
                .ignoreContentType(true)
                .method(pageConnectionParams.getConnectionMethod());

        // Map data to json if content type is application/json
        String contentTypeHeader = pageConnectionParams.getHeaders().get("Content-Type");
        if(contentTypeHeader != null && contentTypeHeader.equals("application/json")) {
            try {
                String requestBody = new ObjectMapper().writeValueAsString(pageConnectionParams.getData());
                connection = connection.requestBody(requestBody);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            for (Map.Entry<String, String> dataES : pageConnectionParams.getData().entrySet()) {
                if (dataES.getKey().endsWith("[]")) {
                    for (String keyArrayPart : dataES.getValue().split(",")) {
                        connection.data(dataES.getKey(), keyArrayPart);
                    }
                } else {
                    connection.data(dataES.getKey(), dataES.getValue());
                }
            }
        }
        return connection;
    }


}
