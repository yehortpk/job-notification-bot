package com.github.yehortpk.notifier.parsers.site;

import com.github.yehortpk.notifier.models.CompanyDTO;
import com.github.yehortpk.notifier.models.VacancyDTO;
import com.github.yehortpk.notifier.parsers.page.PageParser;
import com.github.yehortpk.notifier.services.ProxyService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Setter
@ToString
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.NO)
@Slf4j
public abstract class SiteParserImpl implements SiteParser {
    protected CompanyDTO company;
    @Autowired
    protected ProxyService proxyService;

    @Override
    public Set<VacancyDTO> parseAllVacancies() {
        Document firstPage = parsePage(1);
        List<Document> pages = new ArrayList<>();
        pages.add(firstPage);

        int pagesCount = getPagesCount(firstPage);
        if (pagesCount > 1) {
            List<Future<Document>> futures = new ArrayList<>();
            @Cleanup ThreadPoolExecutor executor =
                    (ThreadPoolExecutor) Executors.newFixedThreadPool(pagesCount - 1);

            for (int pageId = 2; pageId <= pagesCount; pageId++) {
                int finalPageId = pageId;
                Future<Document> future = executor.submit(() -> parsePage(finalPageId));
                futures.add(future);
            }

            for (Future<Document> future : futures) {
                try {
                    pages.add(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    log.error(e.getMessage());
                }
            }
        }

        Set<VacancyDTO> vacancies = new HashSet<>();

        for (Document page : pages) {
            List<Element> vacancyBlocks = getVacancyBlocks(page);

            for (Element vacancyBlock : vacancyBlocks) {
                VacancyDTO vacancy = getVacancyFromBlock(vacancyBlock);
                vacancy.setCompanyID(company.getCompanyId());
                vacancy.setCompanyTitle(company.getTitle());
                vacancies.add(vacancy);
            }
        }

        return vacancies;
    }

    private Document parsePage(int pageId) {
        PageParser pageParser = createPageParser(pageId);

        return pageParser.parsePage();
    }

    public Map<String, String> createHeaders() {
        return new HashMap<>(company.getHeaders());
    }

    protected Map<String, String> createData(int pageId) {
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
    protected abstract PageParser createPageParser(int pageId);
}
