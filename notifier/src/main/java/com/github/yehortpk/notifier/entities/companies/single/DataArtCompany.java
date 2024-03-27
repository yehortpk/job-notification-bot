package com.github.yehortpk.notifier.entities.companies.single;

import com.github.yehortpk.notifier.entities.sites.SinglePageCompanySite;
import com.github.yehortpk.notifier.entities.parsers.PageParserImpl;
import com.github.yehortpk.notifier.entities.parsers.SinglePageParser;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("data_art-company")
public class DataArtCompany extends SinglePageCompanySite {
    @Override
    public PageParserImpl createPageParser(String pageUrl, int pageId) {
        SinglePageParser singlePageParser = new SinglePageParser(pageUrl, pageId);
        singlePageParser.setData(createData(pageUrl, pageId));
        singlePageParser.setParseMethod(Connection.Method.GET);
        return singlePageParser;
    }

    @Override
    public String getPageUrl(int pageId) {
        return this.getCompany().getSinglePageRequestLink();
    }

    @Override
    public int getPagesCount(Document doc) {
        return (int) Double.parseDouble(doc.selectFirst("div[data-key=pagesTotal]").text());
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select("div[data-key='items'] > div:has(div[data-key='slug'])");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        return VacancyDTO.builder()
                .title(block.selectFirst("div[data-key=title] > div").text())
                .link(super.getCompany().getLink() + block.selectFirst("div[data-key=slug] > div").text())
                .build();
    }
}
