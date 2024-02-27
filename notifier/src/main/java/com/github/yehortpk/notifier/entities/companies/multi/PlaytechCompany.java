package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.companies.MultiPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("playtech-company")
public class PlaytechCompany extends MultiPageCompanySite {
    @Override
    public int getPagesCount(Document doc) {
        return 1;
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        Elements vacanciesSections = page.select("section.openings-section.opening");
        Element ukraineVacanciesBlock = vacanciesSections.stream()
                .filter((vacancyElem) -> {
                    String vacancyCountry =
                            vacancyElem.selectFirst("header.opening-header h3").text().toLowerCase().strip();
                    return vacancyCountry.contains("ukraine");
                })
                .toList().getFirst();

        return ukraineVacanciesBlock.select("ul.opening-jobs > li > a");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        return VacancyDTO.builder()
                .link(block.attr("href"))
                .title(block.text())
                .build();
    }
}
