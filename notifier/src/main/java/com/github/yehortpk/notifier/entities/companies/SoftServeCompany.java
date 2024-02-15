package com.github.yehortpk.notifier.entities.companies;

import com.github.yehortpk.notifier.entities.MultiplePageCompanySite;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("soft_serve-company")
public class SoftServeCompany extends MultiplePageCompanySite {
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

    @Override
    public Map<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Cookie", "visid_incap_2401886=tBaF0JU+TliT8yqJRINequNCymUAAAAAQUIPAAAAAABCHWT4hR8Ccncu8OaXnvsj; i"+
                "ncap_ses_1185_2401886=IoWXaRqztD3q1T9Mg/dxEOVCymUAAAAAI1vk3MxyTwNF+oc83fhrOQ==; _gcl_au=1.1.778519904"+
                ".1707754216; cookie_consent={\"essential\":[]}; _clck=1tnx269%7C2%7Cfj7%7C0%7C1503; _ga=GA1.1.2110379"+
                "7.1707754225; incap_ses_1367_2401886=KS4AGOMyHVc9+tawOJD4Eu1CymUAAAAA7tXERbM99QolNsV9jHIQMw==; softse"+
                "rve_session=eyJpdiI6IjEwR25BMnpzRklORWFsWnJKcC9GZ" +
                "UE9PSIsInZhbHVlIjoib3FqeDhZVUZpR2JsMjE4UURkWm00dktCMWF1b0lCNDZidUx6d01TWFlIU1VxY3hCdGdzN1ZkL1FWTjlqUzN" +
                "PYzUrclFYNlhnS3dMSHlDOW5tTGtGb3MvWkljemlHWFlOTHphMHdCdERTTVZVVnp5bEo3ZTV3c2twcnkwVWJHTGYiLCJtYWMiOiI0M" +
                "zJmN2NkYTY4MzVkMDJlMDIzMTIxY2JlZWIxNzhmZTc5ZTZiMGIxMmRhZTE3ODUxNDdkN2RjZTlhYjY1ZWUyIiwidGFnIjoiIn0%3D" +
                "; _ga_PV9ZHXP7KK=GS1.1.1707754225.1.1.1707754242.43.0.0;  _clsk=1oj8fu5%7C1707754247340" +
                "%7C1%7C1%7Co.clarity.ms%2Fcollect; _tt_enable_cookie=1; _ttp=J9yqRi2n39fExLEP1ETKvhDqMS1");
        return headers;
    }
}
