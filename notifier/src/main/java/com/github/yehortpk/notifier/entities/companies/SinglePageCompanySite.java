package com.github.yehortpk.notifier.entities.companies;

import com.github.yehortpk.notifier.entities.parsers.MultiPageParser;
import com.github.yehortpk.notifier.entities.parsers.PageParserImpl;
import com.github.yehortpk.notifier.entities.parsers.SinglePageParser;
import lombok.Getter;
import lombok.ToString;
import org.jsoup.Connection;
import org.springframework.stereotype.Component;

@Component
@ToString(callSuper = true)
@Getter
public abstract class SinglePageCompanySite extends CompanySiteImpl {
    @Override
    public PageParserImpl createPageParser(String pageUrl, int pageId) {
        if (pageId == 1) {
            return  new MultiPageParser(pageUrl, pageId);
        }
        SinglePageParser singlePageParser = new SinglePageParser(pageUrl, pageId);
        singlePageParser.setParseMethod(Connection.Method.POST);
        return singlePageParser;
    }

    @Override
    public String getPageUrl(int pageId) {
        if (pageId == 1) {
            return this.getCompany().getJobsTemplateLink().formatted(pageId);
        }

        return this.getCompany().getSinglePageRequestLink();
    }
}
