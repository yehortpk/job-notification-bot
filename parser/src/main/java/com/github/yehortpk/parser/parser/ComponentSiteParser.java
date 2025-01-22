package com.github.yehortpk.parser.parser;

import com.github.yehortpk.parser.scrapper.ComponentPageScrapper;
import com.github.yehortpk.parser.scrapper.PageScrapper;
import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.models.PageDTO;
import lombok.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Site parser based on component frameworks. Has to have single page and special element query selector which will
 * be only available after site complete rendering. It will be a signal to page ready for parsing.
 * Most widely used #root or #app sections
 */
@Component
@ToString(callSuper = true)
@Getter
public abstract class ComponentSiteParser extends SiteParserImpl {
    private final int DELAY_SEC = 2;

    @Override
    protected PageDTO parsePage(PageConnectionParams pageConnectionParams) throws IOException {
        PageScrapper pageScrapper = createComponentPageParser();
        Document page = Jsoup.parse(pageScrapper.scrapPage(pageConnectionParams).getBody());
        return new PageDTO(pageConnectionParams.getPageUrl(),
                pageConnectionParams.getData(), pageConnectionParams.getHeaders(), page);
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
                .build();
    }

    @Override
    protected int setIntervalBetweenPagesSec(){
        return DELAY_SEC;
    }

    protected PageScrapper createComponentPageParser() {
        return new ComponentPageScrapper(createDynamicElementQuerySelector());
    }

    /**
     * Dynamic element query selector which will be available after rendering. Parser is monitoring this element. If it
     * presents on the page it will be a signal to page ready for parsing
     * @return element query selector
     */
    protected abstract String createDynamicElementQuerySelector();
}
