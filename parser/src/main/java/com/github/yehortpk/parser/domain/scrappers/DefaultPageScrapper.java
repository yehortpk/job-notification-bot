package com.github.yehortpk.parser.domain.scrappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.models.ScrapperResponseDTO;
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
    public ScrapperResponseDTO scrapPage(PageConnectionParams pageConnectionParams) throws IOException {
        Connection.Response response = toConnection(pageConnectionParams).execute();
        return new ScrapperResponseDTO(response.headers(), response.body());
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
            connection = connection.data(pageConnectionParams.getData());
        }
        return connection;
    }


}
