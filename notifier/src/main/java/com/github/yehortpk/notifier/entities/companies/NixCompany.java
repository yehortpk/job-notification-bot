package com.github.yehortpk.notifier.entities.companies;

import com.github.yehortpk.notifier.entities.MultiplePageCompanySite;
import com.github.yehortpk.notifier.models.CompanyDTO;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("n-ix")
public class NixCompany extends MultiplePageCompanySite {
    private CompanyDTO company;

    @Override
    public void setCompany(CompanyDTO companyDTO) {
        this.company = companyDTO;
    }

    @Override
    public String getPageTemplateLink() {
        return company.getJobsTemplateLink();
    }

    @Override
    public int getPagesCount(Document doc) {
        Elements pages = doc.select(".page-numbers");
        Element lastPage = pages.get(pages.size() - 2);

        return Integer.parseInt(lastPage.text());
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
                .companyID(company.getCompanyId())
                .vacancyID(vacancyId)
                .title(vacancyTitle)
                .link(link)
                .build();
    }
}
