package com.github.yehortpk.notifier.parsers.site;

import com.github.yehortpk.notifier.models.PageConnectionParams;
import com.github.yehortpk.notifier.parsers.page.MultiPageParser;
import com.github.yehortpk.notifier.parsers.page.PageParser;
import com.github.yehortpk.notifier.scrappers.JsoupPageScrapper;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@ToString(callSuper = true)
@Getter
public abstract class MultiPageSiteParser extends SiteParserImpl {
    @Override
    public PageParser createPageParser(int pageId) {
        String pageUrl = company.getJobsTemplateLink().replace("{page}", String.valueOf(pageId));
        PageConnectionParams pageConnectionParams = PageConnectionParams.builder()
                .data(createData(pageId))
                .headers(createHeaders())
                .pageUrl(pageUrl)
                .build();

        JsoupPageScrapper jsoupPageScrapper = new JsoupPageScrapper(pageConnectionParams, proxyService);

        return new MultiPageParser(jsoupPageScrapper);
    }
}
