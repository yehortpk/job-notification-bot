package com.github.yehortpk.notifier.entities.sites;

import com.github.yehortpk.notifier.entities.parsers.MultiPageParser;
import com.github.yehortpk.notifier.entities.parsers.PageParserImpl;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@ToString(callSuper = true)
@Getter
public abstract class MultiPageCompanySite extends CompanySiteImpl {
    @Override
    public PageParserImpl createPageParser(String pageUrl, int pageId) {
        return new MultiPageParser(pageUrl, pageId);
    }

    @Override
    public String getPageUrl(int pageId) {
        return this.getCompany().getJobsTemplateLink().formatted(pageId);
    }
}
