package com.github.yehortpk.notifier.entities.companies.multi;

import com.github.yehortpk.notifier.entities.companies.MultiPageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("svitla-company")
public class SvitlaCompany extends MultiPageCompanySite {
    @Override
    public int getPagesCount(Document doc) {
        Elements pagination = doc.select("ul.pagination li");
        return Integer.parseInt(pagination.get(pagination.size() - 2).select("a").text());
    }

    @Override
    public List<Element> getVacancyBlocks(Document page) {
        return page.select("a.blog-1__title");
    }

    @Override
    public VacancyDTO getVacancyFromBlock(Element block) {
        return VacancyDTO.builder()
                .title(block.selectFirst("h3").text().strip())
                .link(super.getCompany().getLink() + block.attr("href"))
                .build();
    }
}
