package com.github.yehortpk.parser.parser;

import com.github.yehortpk.parser.models.CompanyDTO;
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
    private final int DELAY_SEC = 2;

    @Override
    protected PageDTO parsePage(PageConnectionParams pageConnectionParams) throws IOException {

        Document doc = Jsoup.parse(defaultPageScrapper.scrapPage(pageConnectionParams).getBody());
        return new PageDTO(pageConnectionParams.getPageUrl(),
                pageConnectionParams.getData(), pageConnectionParams.getHeaders(), doc);
    }

    @Override
    protected PageConnectionParams generatePageConnectionParams(int pageID, CompanyDTO company) {
        String pageUrl = company.getVacanciesURL().replace("{page}", String.valueOf(pageID));
        Map<String, String> data = new HashMap<>(company.getData());
        if (data.containsValue("{page}")) {
            data.replaceAll((key, value) -> value.equals("{page}") ? String.valueOf(pageID) : value);
        }

        return PageConnectionParams.builder()
                .data(data)
                .headers(company.getHeaders())
                .pageUrl(pageUrl)
                .timeoutSec(setPagePullTimeoutSec())
                .build();
    }

    @Override
    protected int setIntervalBetweenPagesSec(){
        return DELAY_SEC;
    }
}
