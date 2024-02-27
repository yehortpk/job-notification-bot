package com.github.yehortpk.notifier.entities.companies.single;

import com.github.yehortpk.notifier.entities.companies.SinglePageCompanySite;
import com.github.yehortpk.notifier.entities.parsers.PageParserImpl;
import com.github.yehortpk.notifier.entities.parsers.SinglePageParser;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("intellect_soft-company")
public class IntellectSoftCompany extends SinglePageCompanySite {
    @Override
    public PageParserImpl createPageParser(String pageUrl, int pageId) {
        SinglePageParser singlePageParser = new SinglePageParser(pageUrl, pageId);
        singlePageParser.setParseMethod(Connection.Method.GET);
        return singlePageParser;
    }

    @Override
    public String getPageUrl(int pageId) {
        return this.getCompany().getSinglePageRequestLink();
    }

    @Override
    public int getPagesCount(Document doc) {
        return 1;
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select("div[data-key=jobs] > div");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        return VacancyDTO.builder()
                .title(block.selectFirst("div > div[data-key=title] > div").text())
                .link(block.selectFirst("div > div[data-key=url] > div").text())
                .build();
    }
}
