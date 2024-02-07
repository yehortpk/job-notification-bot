package com.github.yehortpk.notifier.entities.companies;

import com.github.yehortpk.notifier.entities.MultiplePageCompanySite;
import com.github.yehortpk.notifier.models.CompanyDTO;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("genesis-company")
public class GenesisCompany extends MultiplePageCompanySite {
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
        return 1;
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select("li.position");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        Element linkElement = block.selectFirst("a");
        String link = company.getJobsTemplateLink() + linkElement.attr("href");
        String vacancyTitle =  linkElement.selectFirst("h2").text();

        return VacancyDTO.builder()
                .companyID(company.getCompanyId())
                .title(vacancyTitle)
                .link(link)
                .build();
    }
}
