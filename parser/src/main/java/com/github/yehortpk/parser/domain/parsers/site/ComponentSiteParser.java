package com.github.yehortpk.parser.domain.parsers.site;

import com.github.yehortpk.parser.domain.connectors.PageConnector;
import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.models.PageDTO;
import lombok.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
        PageConnectionParams pageConnectionParams = PageConnectionParams.builder()
                .data(createData(pageId))
                .headers(createHeaders())
                .pageUrl(pageUrl)
                .dynamicElementQuerySelector(createDynamicElementQuerySelector())
                .delay(pageId * DELAY_SEC * 1000)
                .build();

        Document page = Jsoup.parse(componentPageConnector.connectToPage(pageConnectionParams));
        return new PageDTO(pageUrl, pageId, page);
    }


    @Override
    public int getPagesCount(Document document) {
        return 1;
    }

    /**
     * Dynamic element query selector which will be available after rendering. Parser is monitoring this element. If it
     * presents on the page it will be a signal to page ready for parsing
     * @return element query selector
     */
    protected abstract String createDynamicElementQuerySelector();
}
