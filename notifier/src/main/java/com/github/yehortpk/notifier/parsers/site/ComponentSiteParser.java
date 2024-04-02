package com.github.yehortpk.notifier.parsers.site;

import com.github.yehortpk.notifier.models.PageConnectionParams;
import com.github.yehortpk.notifier.parsers.page.ComponentPageParser;
import com.github.yehortpk.notifier.parsers.page.PageParser;
import com.github.yehortpk.notifier.scrappers.SeleniumPageScrapper;
import lombok.Getter;
import lombok.ToString;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ToString(callSuper = true)
@Getter
public abstract class ComponentSiteParser extends SiteParserImpl {
    @Value("${webdriver.chrome.driver}")
    private String chromeDriverPath;

    @Value("${webdriver.chrome.path}")
    private String chromeBinaryPath;

    public PageParser createPageParser(int pageId) {
        PageConnectionParams pageConnectionParams = PageConnectionParams.builder()
                .data(createData(pageId))
                .headers(createHeaders())
                .pageUrl(company.getJobsTemplateLink())
                .build();

        SeleniumPageScrapper pageScrapper = new SeleniumPageScrapper(
                pageConnectionParams,
                proxyService,
                createDynamicElementQuerySelector(),
                chromeDriverPath,
                chromeBinaryPath);

        return new ComponentPageParser(pageScrapper);
    }


    @Override
    public int getPagesCount(Document document) {
        return 1;
    }

    protected abstract String createDynamicElementQuerySelector();
}
