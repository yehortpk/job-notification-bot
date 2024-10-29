package com.github.yehortpk.parser.domain.parsers;

import com.github.yehortpk.parser.domain.connectors.PageConnector;
import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.models.PageDTO;
import lombok.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private PageConnector componentPageConnector;
    private final int DELAY_SEC = 1;

    @Override
    public PageDTO parsePage(int pageId) throws IOException {
        String pageUrl = company.getJobsTemplateLink();
        Map<String, String> data = new HashMap<>(company.getData());
        if (data.containsValue("{page}")) {
            data.replaceAll((key, value) -> value.equals("{page}") ? String.valueOf(pageId) : value);
        }

        PageConnectionParams pageConnectionParams = PageConnectionParams.builder()
                .data(data)
                .headers(company.getHeaders())
                .pageUrl(pageUrl)
                .dynamicElementQuerySelector(createDynamicElementQuerySelector())
                .delay(pageId * DELAY_SEC * 1000)
                .build();

        Document page = Jsoup.parse(componentPageConnector.connectToPage(pageConnectionParams).getBody());
        return new PageDTO(pageUrl, pageId, page);
    }

    /**
     * Dynamic element query selector which will be available after rendering. Parser is monitoring this element. If it
     * presents on the page it will be a signal to page ready for parsing
     * @return element query selector
     */
    protected abstract String createDynamicElementQuerySelector();
}
