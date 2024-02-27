package com.github.yehortpk.notifier.entities.companies;

import com.github.yehortpk.notifier.entities.PageLoader;
import com.github.yehortpk.notifier.entities.parsers.PageParserImpl;
import com.github.yehortpk.notifier.models.CompanyDTO;
import com.github.yehortpk.notifier.models.VacancyDTO;
import com.github.yehortpk.notifier.services.ProxyService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
@Setter
@ToString
@Getter
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.NO)
public abstract class CompanySiteImpl implements CompanySite{
    @Autowired
    ProxyService proxyService;

    private CompanyDTO company;

    @Override
    public Set<VacancyDTO> parseAllVacancies() {
        Set<VacancyDTO> vacancies = new HashSet<>();

        String pageTemplateLink = getPageUrl(1);

        Document firstPage = parsePage(pageTemplateLink, 1);
        List<Document> pages = new ArrayList<>();
        pages.add(firstPage);

        int pagesCount = getPagesCount(firstPage);
        if (pagesCount > 1) {
            List<Future<Document>> futures = new ArrayList<>();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(pagesCount - 1);

            for (int pageId = 2; pageId <= pagesCount; pageId++) {
                String pageUrl = getPageUrl(pageId);

                int finalPageId = pageId;
                Future<Document> future = executor.submit(() -> parsePage(pageUrl, finalPageId));
                futures.add(future);
            }

            for (Future<Document> future : futures) {
                try {
                    Document page = future.get();
                    pages.add(page);
                } catch (InterruptedException | ExecutionException e) {
                    System.out.printf("Page wasn't parsed: " + e.getMessage());
                }
            }

            executor.close();
        }

        for (Document page : pages) {
            List<Element> vacancyBlocks = getVacancyBlocks(page);

            for (Element vacancyBlock : vacancyBlocks) {
                VacancyDTO vacancy = getVacancyFromBlock(vacancyBlock);
                vacancy.setCompanyID(company.getCompanyId());
                vacancies.add(vacancy);
            }
        }

        return vacancies;
    }

    private Document parsePage(String pageUrl, int pageId) {
        PageParserImpl pageParser = createPageParser(pageUrl, pageId);
        pageParser.setHeaders(createHeaders());
        pageParser.setData(createData(pageUrl, pageId));

        return new PageLoader(proxyService, pageParser).loadPage();
    }

    public Map<String, String> createHeaders() {
        return new HashMap<>(company.getHeaders());
    }

    protected Map<String, String> createData(String pageUrl, int pageId) {
        HashMap<String, String> data = new HashMap<>(company.getData());
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (Objects.equals(entry.getValue(), "{page}")) {
                data.put(entry.getKey(), String.valueOf(pageId));
            }
        }
        return data;
    }

    public abstract int getPagesCount(Document doc);
    public abstract List<Element> getVacancyBlocks(Document page);
    public abstract VacancyDTO getVacancyFromBlock(Element block);
    protected abstract PageParserImpl createPageParser(String pageUrl, int pageId);
    protected abstract String getPageUrl(int pageId);
}
