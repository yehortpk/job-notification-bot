package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.sites.MultiPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("adtelligent-company")
public class AdtelligentCompany extends MultiPageCompanySite {
    @Override
    public int getPagesCount(Document doc) {
        return Integer.parseInt(doc.selectFirst(".nav-links > a").text());
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        Elements vacancies = page.select(".careers__jobs-list-item");
        return vacancies.stream()
                .filter((vacancyElem) -> {
                    String vacancyCountries = vacancyElem.selectFirst(".careers__jobs-list-item_location > p")
                            .text().toLowerCase().strip();
                    return vacancyCountries.contains("kyiv") || vacancyCountries.contains("odesa");
                })
                .map((vacancyElem) -> vacancyElem.selectFirst(".careers__jobs-list-item_title a"))
                .toList();
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        return VacancyDTO.builder()
                .title(block.text())
                .link(block.attr("href"))
                .build();
    }
}
