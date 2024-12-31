package com.github.yehortpk.parser.domain.parsers;

import com.github.yehortpk.parser.domain.connectors.PageConnector;
import com.github.yehortpk.parser.exceptions.NoVacanciesOnPageException;
import com.github.yehortpk.parser.models.*;
import com.github.yehortpk.parser.services.ProgressManagerService;
import lombok.Cleanup;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

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
    @Autowired
    protected PageConnector defaultPageConnector;
    @Autowired
    private ProgressManagerService progressManagerService;

    @Override
    public Set<VacancyDTO> parseAllVacancies() {
        int pagesCount;
        progressManagerService.addBar(company.getCompanyId(), company.getTitle(), 1);

        // Parsing first page to retrieve all metadata (total pages count, csrf, required cookies, etc.)
        try {
            CompanySiteMetadata siteMetadata = extractSiteMetadata();
            pagesCount = siteMetadata.getPagesCount();
            company.setData(createData(siteMetadata.getRequestData()));
            company.setHeaders(createHeaders(siteMetadata.getRequestHeaders()));
            progressManagerService.changeBarStepsCount(company.getCompanyId(), pagesCount);
        } catch (Exception e) {
            progressManagerService.markStepError(company.getCompanyId(), 0);
            throw new RuntimeException("Can't extract site metadata, company: %s. Error: %s"
                    .formatted(company.getTitle(), e.getMessage()));
        }

        List<Future<PageDTO>> page_fut = new ArrayList<>();
        @Cleanup ThreadPoolExecutor executor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(pagesCount);

        for (int pageId = 1; pageId <= pagesCount; pageId++) {
            int finalPageId = pageId;
            Future<PageDTO> future = executor.submit(() -> parsePage(finalPageId));
            page_fut.add(future);
        }

        Set<VacancyDTO> vacancies = new HashSet<>();

        ThreadLocal<Integer> pagesCounter = ThreadLocal.withInitial(() -> 0);
        for (Future<PageDTO> future : page_fut) {
            try {
                PageDTO page = future.get();
                Set<VacancyDTO> vacanciesFromPage = extractVacanciesFromPage(page);
                if (vacanciesFromPage.isEmpty()) {
                    throw new NoVacanciesOnPageException(pagesCounter.get() + 1);
                }
                progressManagerService.markStepDone(company.getCompanyId(), pagesCounter.get());
                log.info("Page: {}, data: {} was parsed", page.getPageURL(), page.getPageData());
                vacancies.addAll(vacanciesFromPage);
            } catch (InterruptedException| ExecutionException e) {
                progressManagerService.markStepError(company.getCompanyId(), pagesCounter.get());
                log.error("company: {}, error: {} ", this.company.getTitle(), e.getCause().getMessage());
            } catch (NoVacanciesOnPageException e) {
                progressManagerService.markStepError(company.getCompanyId(), pagesCounter.get());
                log.error("company: {}, no vacancies on page: {} ", this.company.getTitle(), e.getPageId());
            } catch (Exception e) {
                progressManagerService.markStepError(company.getCompanyId(), pagesCounter.get());
                log.error("company: {}, error: {} ", this.company.getTitle(), e.getMessage());
            } finally {
                pagesCounter.set(pagesCounter.get() + 1);
            }
        }

        return vacancies;
    }

    protected boolean isValueBinding(String value) {
        return value.matches("^\\{.*\\}$");
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

        return vacancies;
    }

    /**
     * Creates query headers for the request
     * @return map of headers
     */
    protected Map<String, String> createHeaders(Map<String, String> siteMetadata) {
        HashMap<String, String> headers = new HashMap<>(company.getHeaders());
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String value = entry.getValue();

            headers.put(entry.getKey(), siteMetadata.getOrDefault(value, value));
        }
        return headers;
    }

    /**
     * Creates query data for the request
     * @return map of data
     */
    protected Map<String, String> createData(Map<String, String> siteMetadata) {
        HashMap<String, String> data = new HashMap<>(company.getData());
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String value = entry.getValue();

            data.put(entry.getKey(), siteMetadata.getOrDefault(value, value));
        }
        return data;
    }

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

    protected CompanySiteMetadata extractSiteMetadata() throws IOException {
        String pageUrl = company.getJobsTemplateLink().replace("{page}","1");
        Map<String, String> siteData = company.getData().entrySet().stream()
                .filter((entry) -> !isValueBinding(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, String> siteHeaders = company.getHeaders().entrySet().stream()
                .filter((entry) -> !isValueBinding(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        PageConnectionParams pageConnectionParams = PageConnectionParams.builder()
                .pageUrl(pageUrl)
                .headers(siteHeaders)
                .data(siteData)
                .build();

        ScrapperResponseDTO pageResponse = defaultPageConnector.connectToPage(pageConnectionParams);
        return parseSiteMetadata(pageResponse.getHeaders(), Jsoup.parse(pageResponse.getBody()));
    }

    // Override in implementations to add metadata to requests
    protected CompanySiteMetadata parseSiteMetadata(Map<String, String> headers, Document doc) {
        CompanySiteMetadata defaultMetadata = new CompanySiteMetadata();
        defaultMetadata.setPagesCount(getPagesCount(doc));
        defaultMetadata.setRequestData(new HashMap<>());
        defaultMetadata.setRequestHeaders(new HashMap<>());

        return defaultMetadata;
    }

    protected int getPagesCount(Document doc) {
        return 1;
    }


    protected int setSecIntervalBetweenPages() {
        return 1;
    }
}
