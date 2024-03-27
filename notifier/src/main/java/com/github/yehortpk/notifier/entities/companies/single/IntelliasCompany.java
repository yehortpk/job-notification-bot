package com.github.yehortpk.notifier.entities.companies.single;

import com.github.yehortpk.notifier.entities.sites.SinglePageCompanySite;
import com.github.yehortpk.notifier.entities.parsers.PageParserImpl;
import com.github.yehortpk.notifier.entities.parsers.SinglePageParser;
import com.github.yehortpk.notifier.models.VacancyDTO;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component("intellias-company")
public class IntelliasCompany extends SinglePageCompanySite {
    public int getPagesCount(Document firstPage) {
        final int VACANCIES_PER_PAGE_DEFAULT = 12;

        int totalVacancies = (int) Double.parseDouble(firstPage.selectFirst("div[data-key=total]").text());
        int vacanciesPerPage = firstPage.select("a.stb-item").size();
        if (vacanciesPerPage == 0) {
            vacanciesPerPage = VACANCIES_PER_PAGE_DEFAULT;
        }

        return (int) Math.ceil((double) totalVacancies / vacanciesPerPage);
    }

    @Override
    public PageParserImpl createPageParser(String pageUrl, int pageId) {
        SinglePageParser singlePageParser = new SinglePageParser(pageUrl, pageId);
        singlePageParser.setParseMethod(Connection.Method.POST);
        singlePageParser.setData(createData(pageUrl, pageId));
        return singlePageParser;
    }

    @Override
    public String getPageUrl(int pageId) {
        return this.getCompany().getSinglePageRequestLink();
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select("a.stb-item");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        return VacancyDTO.builder()
                .link(block.attr("href"))
                .title(block.selectFirst(".card_title").text())
                .build();
    }
}

