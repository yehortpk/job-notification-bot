package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.companies.MultiPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("innovecs-company")
public class InnovecsCompany extends MultiPageCompanySite {
    @Override
    public int getPagesCount(Document doc) {
        return Integer.parseInt(doc.selectFirst(".load-more").attr("data-ajax-pages"));
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        Elements vacancies = page.select("a.box--archive-vacancies-v2--vacancies--item");
        return vacancies.stream()
                .filter((vacancyElem) -> {
                    String vacancyCountries = vacancyElem.selectFirst(".info.h4").text().toLowerCase().strip();
                    return (vacancyCountries.contains("ukraine") || vacancyCountries.contains("kyiv")) ||
                            vacancyCountries.equals("remote");
                })
                .toList();
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        return VacancyDTO.builder()
                .title(block.selectFirst(".title-item").text())
                .link(block.attr("href"))
                .build();
    }
}
