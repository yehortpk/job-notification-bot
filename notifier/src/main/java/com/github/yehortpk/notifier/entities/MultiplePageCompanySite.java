package com.github.yehortpk.notifier.entities;

import com.github.yehortpk.notifier.models.VacancyDTO;
import com.github.yehortpk.notifier.services.ProxyService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class MultiplePageCompanySite implements CompanySiteInterface{
    @Autowired
    ProxyService proxyService;

    @Override
    public Set<VacancyDTO> parseAllVacancies() throws IOException {
        Set<VacancyDTO> vacancies = new HashSet<>();

        String pageTemplateLink = getPageTemplateLink();
        String firstPageUrl = String.format(pageTemplateLink, 1);

        Document firstPage = loadPage(firstPageUrl);

        for (int pageId = 1; pageId <= getPagesCount(firstPage); pageId++) {
            String pageUrl = String.format(pageTemplateLink, pageId);

            Document page = loadPage(pageUrl);
            List<Element> vacancyBlocks = getVacancyBlocks(page);

            for (Element vacancyBlock : vacancyBlocks) {
                VacancyDTO vacancy = getVacancyFromBlock(vacancyBlock);
                vacancies.add(vacancy);
            }
        }

        return vacancies;
    }

    private Document loadPage(String pageURL) throws IOException {
        return Jsoup.connect(pageURL).get();
    }

    private Document loadPage(String pageURL, boolean enableProxies) throws IOException {
        if (enableProxies) {
            // 5 seconds
            int timeoutMillis = 5000;

            while (true) {
                Proxy randomProxy = proxyService.getRandomProxy();
                try {
                    return loadPage(pageURL, randomProxy, timeoutMillis);
                } catch (IOException ignored) {
                    System.out.println("timeout error: " + randomProxy);
                }
            }
        } else {
            return loadPage(pageURL);
        }
    }

    private Document loadPage(String pageURL, Proxy proxy, int timeout) throws IOException {
        return Jsoup.connect(pageURL).proxy(proxy).timeout(timeout).get();
    }

    public abstract String getPageTemplateLink();
    public abstract int getPagesCount(Document doc);
    public abstract List<Element> getVacancyBlocks(Document page);
    public abstract VacancyDTO getVacancyFromBlock(Element block);
}
