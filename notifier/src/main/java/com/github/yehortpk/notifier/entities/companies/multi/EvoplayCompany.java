package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.companies.MultiPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("evoplay-company")
public class EvoplayCompany extends MultiPageCompanySite {
    @Override
    public int getPagesCount(Document doc) {
        return 1;
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select("a[href*=\"/careers\"]");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        String title = block.selectFirst("div.row > div").text();

        String link = block.attr("href");
        int endIndex = link.indexOf('?');
        if (endIndex > 0) {
            link = link.substring(0, endIndex);
        }
        return VacancyDTO.builder()
                .title(title)
                .link(super.getCompany().getLink() + link)
                .build();
    }
}
