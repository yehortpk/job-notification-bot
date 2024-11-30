package com.github.yehortpk.parser.domain.parsers;

import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.models.PageDTO;
import lombok.*;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Site parser based on common page pattern but with special page info
 */
@Component
@ToString(callSuper = true)
@Getter
public abstract class StaticSiteParser extends SiteParserImpl {
    private final int DELAY_SEC = 1;

    @Override
    public PageDTO parsePage(int pageId) throws IOException {
        String pageUrl = company.getJobsTemplateLink().replace("{page}", String.valueOf(pageId));
        Map<String, String> data = new HashMap<>(company.getData());
        if (data.containsValue("{page}")) {
            data.replaceAll((key, value) -> value.equals("{page}") ? String.valueOf(pageId) : value);
        }

        PageConnectionParams pageConnectionParams = PageConnectionParams.builder()
                .data(data)
                .headers(company.getHeaders())
                .pageUrl(pageUrl)
                .delay(pageId * DELAY_SEC * 1000)
                .build();

        Document doc = Jsoup.parse(defaultPageConnector.connectToPage(pageConnectionParams).getBody());
        return new PageDTO(pageUrl, pageId, data, doc);
    }
}
