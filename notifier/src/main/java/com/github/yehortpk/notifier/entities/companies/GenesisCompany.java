package com.github.yehortpk.notifier.entities.companies;

import com.github.yehortpk.notifier.entities.MultiplePageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import lombok.ToString;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("genesis-company")
@ToString(callSuper = true)
public class GenesisCompany extends MultiplePageCompanySite {
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

    @Override
    public Map<String, String> getHeaders() {
        HashMap<String, String> result = new HashMap<>();
        result.put("Cookie", "_ga=GA1.2.1778922810.1707678925; _gid=GA1.2.6595246.1707678925; _gat=1; _ga_RJW" +
                "NTZKGN2=GS1.2.1707678932.1.1.1707678942.50.0.0");
        result.put("Sec-Ch-Ua", "Not A(Brand\";v=\"99\", \"Google Chrome\";v=\"121\", \"Chromium\";v=\"121\"");
        result.put("Sec-Ch-Ua-Platform", "Windows");
        result.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng," +
                "*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");

        return result;
    }
}
