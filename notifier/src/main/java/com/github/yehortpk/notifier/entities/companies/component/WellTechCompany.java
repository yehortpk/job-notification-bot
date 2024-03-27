package com.github.yehortpk.notifier.entities.companies.component;

import com.github.yehortpk.notifier.entities.sites.ComponentPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("well_tech-company")
public class WellTechCompany extends ComponentPageCompanySite {
    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select("a[class*=_container]");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        return VacancyDTO.builder()
                .title(block.selectFirst("h3").text())
                .link(super.getCompany().getLink() + block.attr("href"))
                .build();
    }

    @Override
    protected String createDynamicElementQuerySelector() {
        return "#root div[class*=_content]";
    }
}
