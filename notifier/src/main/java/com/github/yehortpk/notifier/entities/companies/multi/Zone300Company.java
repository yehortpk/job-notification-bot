package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.sites.MultiPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("zone300-company")
public class Zone300Company extends MultiPageCompanySite {
    @Override
    public int getPagesCount(Document doc) {
        return 1;
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select("a[href*='#vacancy-']");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        return VacancyDTO.builder()
                .title(block.text())
                .link(super.getCompany().getLink() + block.attr("href"))
                .build();
    }
}
