package com.github.yehortpk.notifier.entities.companies.component;

import com.github.yehortpk.notifier.entities.sites.ComponentPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("netpeak-company")
public class NetPeakCompany extends ComponentPageCompanySite {
    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select(".card-content");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        Element linkElement = block.parent();

        return VacancyDTO.builder()
                .link(super.getCompany().getLink() + linkElement.attr("href"))
                .title(block.selectFirst(".card-title-2").text())
                .build();
    }

    @Override
    protected String createDynamicElementQuerySelector() {
        return "#app > section";
    }
}
