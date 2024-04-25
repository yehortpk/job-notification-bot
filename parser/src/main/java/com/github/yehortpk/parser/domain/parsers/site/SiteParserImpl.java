package com.github.yehortpk.parser.domain.parsers.site;

import com.github.yehortpk.parser.domain.connectors.PageConnector;
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

/**
 * Default abstract implementation of {@link SiteParser}. Provides default common dataflow from a page connection and
 * scrapping to parsing and creating {@link VacancyDTO} object. </br>
 * <p></p>
 * Required methods that will inherit from this class:
 * <ul>
 * <li>getPagesCount - the number of pages on the site</li>
 * <li>extractVacancyBlocksFromPage - extract blocks of vacancies from {@link Document} page</li>
 * <li>generateVacancyObjectFromBlocks - extract {@link VacancyDTO} vacancy object from vacancy block</li>
 * </ul>
 */
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

    /**
     * Extract vacancies from {@link Document} page
     * @param page page
     * @return set of vacancies from page
     */
    private Set<VacancyDTO> extractVacanciesFromPage(PageDTO page) {
        Set<VacancyDTO> vacancies = new HashSet<>();
        List<Element> vacancyBlocks = extractVacancyBlocksFromPage(page.getDoc());

        for (Element vacancyBlock : vacancyBlocks) {
            VacancyDTO vacancy = generateVacancyObjectFromBlock(vacancyBlock);
            vacancy.setCompanyID(company.getCompanyId());
            vacancy.setCompanyTitle(company.getTitle());
            vacancies.add(vacancy);
        }

        log.info("Page: {} parsed", page.getPageURL());
        return vacancies;
    }

    /**
     * Creates query headers for the request
     * @return map of headers
     */
    public Map<String, String> createHeaders() {
        if (company.getHeaders() == null) {
            return new HashMap<>();
        }
        return new HashMap<>(company.getHeaders());
    }

    /**
     * Creates query data for the request
     * @param pageId id of the page
     * @return map of data
     */
    protected Map<String, String> createData(int pageId) {
        if (company.getData() == null) {
            return new HashMap<>();
        }
        HashMap<String, String> data = new HashMap<>(company.getData());
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (Objects.equals(entry.getValue(), "{page}")) {
                data.put(entry.getKey(), String.valueOf(pageId));
            }
        }
        return data;
    }

    /**
     * Returns number of pages with vacancies on the site
     * @param page page
     * @return number of pages
     */
    public abstract int getPagesCount(Document page);
    /**
     * Extracts blocks with vacancy from the page
     * @param page page
     * @return blocks of {@link Element} pages with vacancies
     */
    public abstract List<Element> extractVacancyBlocksFromPage(Document page);
    /**
     * Generates {@link VacancyDTO} object from block source
     * @param block vacancy block
     * @return vacancy object
     */
    public abstract VacancyDTO generateVacancyObjectFromBlock(Element block);

    /**
     * Parse page by pageId. Returns Jsoup {@link Document} object with metadata that represents the page
     * @param pageId number of a page
     * @return {@link PageDTO} page object
     * @throws IOException delegates error from {@link PageConnector}
     */
    protected abstract PageDTO parsePage(int pageId) throws IOException;
}
