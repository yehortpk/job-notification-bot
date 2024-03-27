package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.sites.MultiPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import lombok.ToString;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("genesis-company")
@ToString(callSuper = true)
public class GenesisCompany extends MultiPageCompanySite {
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
        String link = super.getCompany().getLink() + linkElement.attr("href");
        String vacancyTitle =  linkElement.selectFirst("h2").text();

        return VacancyDTO.builder()
                .title(vacancyTitle)
                .link(link)
                .build();
    }
}
