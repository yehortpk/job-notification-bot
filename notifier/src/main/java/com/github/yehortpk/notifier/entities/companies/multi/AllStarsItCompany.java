package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.companies.MultiPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("allstars-company")
public class AllStarsItCompany extends MultiPageCompanySite {
    @Override
    public int getPagesCount(Document doc) {
        // Site work only with js, so without it return 1 page
//        int vacanciesPerPage = Integer.parseInt(
//                doc.select("div.pag-num-div.sh:not(.hero-block) > div:nth-child(2)").text());
//
//        int totalVacancies = Integer.parseInt(
//                doc.select("div.pag-num-div.sh:not(.hero-block) > div:nth-child(4)").text());
//
//        return (int) Math.ceil((double) totalVacancies / vacanciesPerPage);
        return 1;
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select("div.job-item:has(div.text-block-202:contains(Ukraine))");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        Element linkBlock = block.selectFirst("a");
        String vacancyTitle = linkBlock.selectFirst("div.text-block-90").text();

        return VacancyDTO.builder()
                .link(super.getCompany().getLink() + linkBlock.attr("href"))
                .title(vacancyTitle)
                .build();
    }
}
