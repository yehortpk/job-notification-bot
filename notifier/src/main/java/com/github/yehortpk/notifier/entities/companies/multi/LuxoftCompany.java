package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.companies.MultiplePageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("luxoft-company")
public class LuxoftCompany extends MultiplePageCompanySite {

    @Override
    public int getPagesCount(Document doc) {
        Elements pages = doc.select("ul.pagination > li > a");
        return Integer.parseInt(pages.get(pages.size() - 2).text());
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select("tr[data-offers-id]");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        Element linkElement = block.selectFirst("a[data-offers]");
        String link = super.getCompany().getLink() + linkElement.attr("href");
        String vacancyTitle = linkElement.text();
        String vacancySeniority = block.select("td").get(3).text();

        return VacancyDTO.builder()
                .link(link)
                .title(vacancySeniority + " " + vacancyTitle)
                .build();

    }
}
