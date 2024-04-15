package com.github.yehortpk.parser.domain.parsers.site;

import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.models.PageDTO;
import com.github.yehortpk.parser.models.VacancyDTO;
 import lombok.Cleanup;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    @Override
    public Set<VacancyDTO> parseAllVacancies() {
        PageDTO firstPage;
        try {
            firstPage = parsePage(1);
        } catch (IOException e) {
            throw new RuntimeException("Can't parse vacancies for company: %s. Error: %s"
                    .formatted(company.getTitle(), e.getMessage()));
        }
        List<PageDTO> pages = new ArrayList<>();
        pages.add(firstPage);

        int pagesCount = getPagesCount(firstPage.getDoc());
        if (pagesCount > 1) {
            List<Future<PageDTO>> futures = new ArrayList<>();
            @Cleanup ThreadPoolExecutor executor =
                    (ThreadPoolExecutor) Executors.newFixedThreadPool(pagesCount - 1);

            for (int pageId = 2; pageId <= pagesCount; pageId++) {
                int finalPageId = pageId;
                Future<PageDTO> future = executor.submit(() -> parsePage(finalPageId));
                futures.add(future);
            }

            for (Future<PageDTO> future : futures) {
                try {
                    pages.add(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    log.error(e.getMessage());
                }
            }
        }

        Set<VacancyDTO> vacancies = new HashSet<>();

        for (PageDTO page : pages) {
            vacancies.addAll(extractVacanciesFromPage(page));
        }

        return vacancies;
    }

    private Set<VacancyDTO> extractVacanciesFromPage(PageDTO page) {
        Set<VacancyDTO> vacancies = new HashSet<>();
        List<Element> vacancyBlocks = getVacancyBlocks(page.getDoc());

        for (Element vacancyBlock : vacancyBlocks) {
            VacancyDTO vacancy = getVacancyFromBlock(vacancyBlock);
            vacancy.setCompanyID(company.getCompanyId());
            vacancy.setCompanyTitle(company.getTitle());
            vacancies.add(vacancy);
        }

        log.info("Page: {} parsed", page.getPageURL());
        return vacancies;
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
    protected abstract PageDTO parsePage(int pageId) throws IOException;
}
