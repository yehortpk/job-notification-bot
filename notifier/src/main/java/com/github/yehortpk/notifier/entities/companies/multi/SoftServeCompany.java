package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.sites.MultiPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("soft_serve-company")
public class SoftServeCompany extends MultiPageCompanySite {
    @Override
    public int getPagesCount(Document doc) {
//        return Integer.parseInt(doc.select("li.p-rel").last().selectFirst("a").text());
        return 5;
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select("a.vacancy");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        String title = block.selectFirst(".vacancy-title").text();
        String link = block.attr("href");

        return VacancyDTO.builder()
                .title(title)
                .link(link)
                .build();
    }
}
