package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.companies.MultiPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("tema_bit-company")
public class TemaBitCompany extends MultiPageCompanySite {
    @Override
    public int getPagesCount(Document doc) {
        Elements menuElements = doc.select("ul.jobsearch-page-numbers > li");
        return Integer.parseInt(menuElements.get(menuElements.size() - 2).text());
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select(".jobsearch-job li[class*='jobsearch-column']");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        Element linkElement = block.selectFirst("h2.jobsearch-pst-title > a");
        return VacancyDTO.builder()
                .link(linkElement.attr("href"))
                .title(linkElement.text())
                .build();
    }
}
