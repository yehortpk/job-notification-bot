package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.companies.MultiplePageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("global_logic-company")
public class GlobalLogicCompany extends MultiplePageCompanySite {

    @Override
    public int getPagesCount(Document doc) {
        int vacanciesPerPageInitial = 10;
        int vacanciesCount = Integer.parseInt(doc.select(".filter-main>h5").text().split(" ")[0]);
        return vacanciesCount/vacanciesPerPageInitial + (vacanciesCount % vacanciesPerPageInitial > 0? 1: 0);
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select(".career-pagelink");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        Element linkElement = block.selectFirst("a");

        return VacancyDTO.builder()
                .link(linkElement.attr("href"))
                .title(linkElement.text())
                .build();
    }
}
