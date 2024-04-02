package com.github.yehortpk.notifier.parsers.site;

import com.github.yehortpk.notifier.models.PageConnectionParams;
import com.github.yehortpk.notifier.parsers.page.MultiPageParser;
import com.github.yehortpk.notifier.parsers.page.PageParser;
import com.github.yehortpk.notifier.parsers.page.XHRPageParser;
import com.github.yehortpk.notifier.scrappers.JsoupPageScrapper;
import lombok.Getter;
import lombok.ToString;
import org.jsoup.Connection;
import org.springframework.stereotype.Component;

@Component
@ToString(callSuper = true)
@Getter
public abstract class XHRSiteParser extends SiteParserImpl {
    public PageParser createPageParser(int pageId) {
        PageConnectionParams pageConnectionParams = PageConnectionParams.builder()
                .data(createData(pageId))
                .headers(createHeaders())
                .pageUrl(company.getJobsTemplateLink())
                .build();

        if (pageId == 1) {
            JsoupPageScrapper jsoupPageScrapper = new JsoupPageScrapper(pageConnectionParams, proxyService);
            return new MultiPageParser(jsoupPageScrapper);
        }
        pageConnectionParams.setPageUrl(company.getSinglePageRequestLink());
        pageConnectionParams.setConnectionMethod(Connection.Method.POST);
        JsoupPageScrapper jsoupPageScrapper = new JsoupPageScrapper(pageConnectionParams, proxyService);

        return new XHRPageParser(jsoupPageScrapper);
    }
}
