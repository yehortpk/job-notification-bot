package com.github.yehortpk.notifier.entities.companies.single;

import com.github.yehortpk.notifier.entities.sites.SinglePageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Component("sigma_software-company")
public class SigmaSoftwareCompany extends SinglePageCompanySite {
    public int getPagesCount(Document firstPage) {
        final int DEFAULT_VACANCIES_PER_PAGE = 8;
        String vacanciesCountBlock = firstPage.selectFirst(".search-results").text();
        int totalVacancies = Integer.parseInt(vacanciesCountBlock.split("\\D+")[1]);
        int vacanciesPerPage = firstPage.select("a.card").size();

        if (vacanciesPerPage == 0) {
            vacanciesPerPage = DEFAULT_VACANCIES_PER_PAGE;
        }

        return (int) Math.ceil((double) totalVacancies / vacanciesPerPage);
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select("a.card");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        return VacancyDTO.builder()
                .link(block.attr("href"))
                .title(block.selectFirst(".card-title").text())
                .build();
    }

    @Override
    protected Map<String, String> createData(String pageUrl, int pageId) {
        Map<String, String> data = new HashMap<>();
        data.put("action", "load_more_vacancies_new");
        data.put("current_page", String.valueOf(pageId));
        data.put("locations[]", "ukraine");
        data.put("is_subcontract", "false");

        return data;
    }
}

