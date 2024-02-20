package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.companies.MultiplePageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("ciklum-company")
public class CiklumCompany extends MultiplePageCompanySite {
    @Override
    public int getPagesCount(Document doc) {
        Elements pages = doc.select("a.page-numbers");
        return Integer.parseInt(pages.get(pages.size() - 2).text());
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select(".vacancy-card__inner:not(.u-hd)");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        Element linkElement = block.selectFirst("a.vacancy-card__title");
        return VacancyDTO.builder()
                .title(linkElement.text())
                .link(linkElement.attr("href"))
                .build();
    }
}
