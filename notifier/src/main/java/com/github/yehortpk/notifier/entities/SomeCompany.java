package com.github.yehortpk.notifier.entities;

import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SomeCompany extends MultiplePageCompanySite {
    // todo add company data db
    private final int companyId = 1;

    @Override
    public String getPageTemplateLink() {
        return "https://careers.n-ix.com/jobs/page/%s/";
    }

    @Override
    public int getPagesCount(Document doc) {
        return 3;
//        Elements pages = doc.select(".page-numbers");
//        Element lastPage = pages.get(pages.size() - 2);
//
//        return Integer.parseInt(lastPage.text());
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select(".job-card-sm");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        Element linkElement = block.selectFirst(".top-title-info > a");
        String link = linkElement.attr("href");
        String vacancyTitle = linkElement.text();
        // Change to regex
        int vacancyId = Integer.parseInt(vacancyTitle.split("#")[1].split("\\)")[0]);

        return VacancyDTO.builder()
                .companyID(companyId)
                .vacancyID(vacancyId)
                .title(vacancyTitle)
                .link(link)
                .build();
    }
}
