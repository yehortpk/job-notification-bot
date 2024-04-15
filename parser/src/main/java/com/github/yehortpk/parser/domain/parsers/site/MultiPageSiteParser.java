package com.github.yehortpk.parser.domain.parsers.site;

import com.github.yehortpk.parser.domain.connectors.PageConnector;
import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.models.PageDTO;
import lombok.*;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.jsoup.nodes.Document;

import java.io.IOException;

@Component
@ToString(callSuper = true)
@Getter
public abstract class MultiPageSiteParser extends SiteParserImpl {
    @Autowired
    private PageConnector defaultPageScrapperLoader;
    private final int DELAY_SEC = 1;

    public PageDTO parsePage(int pageId) throws IOException {
        String pageUrl = company.getJobsTemplateLink().replace("{page}", String.valueOf(pageId));
        PageConnectionParams pageConnectionParams = PageConnectionParams.builder()
                .data(createData(pageId))
                .headers(createHeaders())
                .pageUrl(pageUrl)
                .delay(pageId * DELAY_SEC * 1000)
                .build();

        Document doc = Jsoup.parse(defaultPageScrapperLoader.connectToPage(pageConnectionParams));
        return new PageDTO(pageUrl, pageId, doc);
    }
}
