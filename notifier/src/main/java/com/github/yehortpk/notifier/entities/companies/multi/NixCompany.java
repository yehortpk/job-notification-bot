package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.companies.MultiplePageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import lombok.ToString;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("n_ix-company")
@ToString(callSuper = true)
public class NixCompany extends MultiplePageCompanySite {
    @Override
    public int getPagesCount(Document doc) {
        Elements pages = doc.select(".page-numbers");
        Element lastPage = pages.get(pages.size() - 2);

        return Integer.parseInt(lastPage.text());
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select(".job-card-sm");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        Element linkElement = block.selectFirst(".top-title-info > a");
        String link = linkElement.attr("href");
        String vacancyTitle = linkElement.text();

        return VacancyDTO.builder()
                .title(vacancyTitle)
                .link(link)
                .build();
    }

    @Override
    public Map<String, String> createHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Connection", "keep-alive");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        return headers;
    }
}
