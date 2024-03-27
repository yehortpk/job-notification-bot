package com.github.yehortpk.notifier.entities.sites;

import com.github.yehortpk.notifier.entities.parsers.ComponentPageParser;
import com.github.yehortpk.notifier.entities.parsers.PageParserImpl;
import lombok.Getter;
import lombok.ToString;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ToString(callSuper = true)
@Getter
public abstract class ComponentPageCompanySite extends CompanySiteImpl {
    @Value("${webdriver.chrome.driver}")
    private String chromeDriverPath;

    @Value("${webdriver.chrome.path}")
    private String chromePath;

    @Override
    public PageParserImpl createPageParser(String pageUrl, int pageId) {
        ComponentPageParser componentPageParser = new ComponentPageParser(pageUrl, pageId);
        componentPageParser.setDynamicElementQuerySelector(createDynamicElementQuerySelector());
        componentPageParser.setChromePath(chromePath);
        componentPageParser.setChromeDriverPath(chromeDriverPath);

        return componentPageParser;
    }

    @Override
    public String getPageUrl(int pageId) {
        return this.getCompany().getJobsTemplateLink();
    }

    @Override
    public int getPagesCount(Document document) {
        return 1;
    }

    protected abstract String createDynamicElementQuerySelector();
}
