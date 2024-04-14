package com.github.yehortpk.notifier.domain.parsers.site;

import com.github.yehortpk.notifier.domain.connectors.PageConnector;
import com.github.yehortpk.notifier.models.PageConnectionParams;
import com.github.yehortpk.notifier.models.PageDTO;
import lombok.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@ToString(callSuper = true)
@Getter
public abstract class ComponentSiteParser extends SiteParserImpl {
    @Autowired
    private PageConnector componentPageScrapperLoader;
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

        Document page = Jsoup.parse(componentPageScrapperLoader.connectToPage(pageConnectionParams));
        return new PageDTO(pageUrl, pageId, page);
    }


    @Override
    public int getPagesCount(Document document) {
        return 1;
    }
    protected abstract String createDynamicElementQuerySelector();
}
