package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.companies.MultiPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("autodoc-company")
public class AutodocCompany extends MultiPageCompanySite {
    @Override
    public int getPagesCount(Document doc) {
        final int VACANCIES_PER_PAGE_DEFAULT = 25;

        String vacanciesCountBlock = doc.selectFirst(".all-jobs-block__find").text();
        int totalVacancies = Integer.parseInt(vacanciesCountBlock.split("\\D+")[0]);
        int vacanciesPerPage = doc.select(".all-jobs-block__table tbody > tr").size();

        if (vacanciesPerPage == 0) {
            vacanciesPerPage = VACANCIES_PER_PAGE_DEFAULT;
        }

        return (int) Math.ceil((double) totalVacancies / vacanciesPerPage);
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select(".all-jobs-block__table tbody > tr a");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        return VacancyDTO.builder()
                .title(block.text())
                .link(block.attr("href"))
                .build();
    }
}
