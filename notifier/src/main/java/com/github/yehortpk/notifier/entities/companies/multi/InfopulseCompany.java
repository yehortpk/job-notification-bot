package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.companies.MultiplePageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("infopulse-company")
public class InfopulseCompany extends MultiplePageCompanySite {

    @Override
    public int getPagesCount(Document doc) {
        final int VACANCIES_PER_PAGE_DEFAULT = 10;

        String vacanciesCountBlock = doc.selectFirst(".jobs__container__result__amount").text();
        int totalVacancies = Integer.parseInt(vacanciesCountBlock.split("\\D+")[1]);
        int vacanciesPerPage = doc.select(".job-card:not(.hidden)").size();

        if (vacanciesPerPage == 0) {
            vacanciesPerPage = VACANCIES_PER_PAGE_DEFAULT;
        }

        return (int) Math.ceil((double) totalVacancies / vacanciesPerPage);
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select(".job-card:not(.hidden)");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        Element linkElement = block.selectFirst("a.job-card__title");
        return VacancyDTO.builder()
                .title(linkElement.text())
                .link(super.getCompany().getLink() + linkElement.attr("href"))
                .build();
    }
}
