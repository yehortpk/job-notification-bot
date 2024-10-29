package com.github.yehortpk.parser.domain.scrappers;

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
                .userAgent(pageConnectionParams.getUserAgent())
                .proxy(pageConnectionParams.getProxy())
                .headers(pageConnectionParams.getHeaders())
                .data(pageConnectionParams.getData())
                .ignoreContentType(true)
                .method(pageConnectionParams.getConnectionMethod());
        String requestBody = pageConnectionParams.getRequestBody();
        if (requestBody != null) {
            connection.requestBody(requestBody);
        }
        return connection;
    }


}
