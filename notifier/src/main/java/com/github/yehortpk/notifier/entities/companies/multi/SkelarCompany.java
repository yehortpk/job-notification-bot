package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.sites.MultiPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("skelar-company")
public class SkelarCompany extends MultiPageCompanySite {
    @Override
    public int getPagesCount(Document doc) {
        return 1;
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select(".position");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        Element linkElement = block.selectFirst("a");

        return VacancyDTO.builder()
                .link(super.getCompany().getLink() + linkElement.attr("href"))
                .title(linkElement.selectFirst("h2").text())
                .build();
    }
}
