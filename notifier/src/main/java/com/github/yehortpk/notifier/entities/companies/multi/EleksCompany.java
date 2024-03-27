package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.sites.MultiPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("eleks-company")
public class EleksCompany extends MultiPageCompanySite {
    @Override
    public int getPagesCount(Document doc) {
        return 1;
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select(".vacancy-item");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        return VacancyDTO.builder()
                .link(block.attr("href"))
                .title(block.selectFirst(".vacancy-item__title").text())
                .build();
    }
}
