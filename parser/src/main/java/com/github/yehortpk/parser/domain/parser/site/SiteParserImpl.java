package com.github.yehortpk.parser.domain.parser.site;

import com.github.yehortpk.parser.domain.parser.page.PageParserFactory;
import com.github.yehortpk.parser.domain.parser.page.PageParser;
import com.github.yehortpk.parser.exceptions.NoVacanciesOnPageException;
import com.github.yehortpk.parser.models.*;
import com.github.yehortpk.parser.services.ProgressManagerService;
import lombok.Cleanup;
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
@ToString
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.NO)
@Slf4j
public abstract class SiteParserImpl implements SiteParser {
    protected CompanyDTO company;
    protected PageParser defaultPageParser;

    @Autowired
    protected PageParserFactory pageParserFactory;

    @Autowired
    private ProgressManagerService progressManagerService;

    @Override
    public Set<VacancyDTO> parseVacancies(CompanyDTO company) {
        this.defaultPageParser = pageParserFactory.createDefaultPageParser();
        this.company = company;

        int pagesCount = 1;
        progressManagerService.addBar(company.getCompanyId(), company.getTitle(), 1);
        if (!isCompanyNeedMetadata(company)) {
            progressManagerService.setMetadataStatus(company.getCompanyId(), MetadataStatusEnum.DONE);
        } else {

            // Parsing first page to retrieve all metadata (total pages count, csrf, required cookies, etc.)
            try {
                CompanySiteMetadata siteMetadata = extractSiteMetadata(company);
                pagesCount = siteMetadata.getPagesCount();
                company.setData(createData(siteMetadata.getRequestData(), company.getData()));
                company.setHeaders(createHeaders(siteMetadata.getRequestHeaders(), company.getHeaders()));
                progressManagerService.setMetadataStatus(company.getCompanyId(), MetadataStatusEnum.DONE);
                progressManagerService.changeBarStepsCount(company.getCompanyId(), pagesCount);
            } catch (Exception e) {
                progressManagerService.setMetadataStatus(company.getCompanyId(), MetadataStatusEnum.ERROR);
                progressManagerService.markStepError(company.getCompanyId(), 0);
                throw new RuntimeException("Can't extract site metadata, company: %s. Error: %s"
                        .formatted(company.getTitle(), e.getMessage()));
            }
        }

        List<Future<PageDTO>> page_fut = new ArrayList<>();
        @Cleanup ThreadPoolExecutor executor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(pagesCount);

        for (int pageId = 1; pageId <= pagesCount; pageId++) {
            PageConnectionParams pageConnectionParams = generatePageConnectionParams(pageId, company);
            int finalPageId = pageId;
            Future<PageDTO> future = executor.submit(() -> {
                Thread.sleep((finalPageId - 1) * setIntervalBetweenPagesSec() * 1000L);
                return parsePage(pageConnectionParams);
            });
            page_fut.add(future);
        }

        Set<VacancyDTO> vacancies = new HashSet<>();

        ThreadLocal<Integer> pagesCounter = ThreadLocal.withInitial(() -> 0);
        for (Future<PageDTO> future : page_fut) {
            try {
                PageDTO page = future.get();
                Set<VacancyDTO> vacanciesFromPage = extractVacanciesFromPage(page);
                vacanciesFromPage.forEach(vacancy -> {
                    vacancy.setCompanyID(company.getCompanyId());
                    vacancy.setCompanyTitle(company.getTitle());
                });
                if (vacanciesFromPage.isEmpty()) {
                    throw new NoVacanciesOnPageException(pagesCounter.get() + 1);
                }
                progressManagerService.markStepDone(company.getCompanyId(), pagesCounter.get());
                log.info("Page: {}, data: {} was parsed", page.getPageURL(), page.getPageData());
                vacancies.addAll(vacanciesFromPage);
            } catch (InterruptedException| ExecutionException e) {
                progressManagerService.markStepError(company.getCompanyId(), pagesCounter.get());
                log.error("company: {}, error: {} ", company.getTitle(), e.getCause().getMessage());
            } catch (NoVacanciesOnPageException e) {
                progressManagerService.markStepError(company.getCompanyId(), pagesCounter.get());
                log.error("company: {}, no vacancies on page: {} ", company.getTitle(), e.getPageId());
            } catch (Exception e) {
                progressManagerService.markStepError(company.getCompanyId(), pagesCounter.get());
                log.error("company: {}, error: {} ", company.getTitle(), e.getMessage());
            } finally {
                pagesCounter.set(pagesCounter.get() + 1);
            }
        }

        return vacancies;
    }

    private boolean isCompanyNeedMetadata(CompanyDTO company) {
        for (Map.Entry<String, String> dataEntry : company.getData().entrySet()) {
            if (isValueBinding(dataEntry.getValue())) {
                return true;
            }
        }

        for (Map.Entry<String, String> headerEntry : company.getHeaders().entrySet()) {
            if (isValueBinding(headerEntry.getValue())) {
                return true;
            }
        }

        return isValueBinding(company.getVacanciesURL());
    }

    protected boolean isValueBinding(String value) {
        return value.contains("{") && value.contains("}") && value.indexOf('{') < value.indexOf('}');
    }

    /**
     * Extract vacancies from {@link Document} page
     * @param page page
     * @return set of vacancies from page
     */
    private Set<VacancyDTO> extractVacanciesFromPage(PageDTO page) {
        Set<VacancyDTO> vacancies = new HashSet<>();
        List<Element> vacancyBlocks = extractVacancyElementsFromPage(page.getDoc());

        for (Element vacancyBlock : vacancyBlocks) {
            VacancyDTO vacancy = generateVacancyFromElement(vacancyBlock);
            vacancies.add(vacancy);
        }

        return vacancies;
    }

    /**
     * Creates query headers for the request
     * @return map of headers
     */
    protected Map<String, String> createHeaders(Map<String, String> siteMetadata, Map<String, String> companyHeaders) {
        HashMap<String, String> headers = new HashMap<>(companyHeaders);
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
    protected Map<String, String> createData(Map<String, String> siteMetadata, Map<String, String> companyData) {
        HashMap<String, String> data = new HashMap<>(companyData);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String value = entry.getValue();

            data.put(entry.getKey(), siteMetadata.getOrDefault(value, value));
        }
        return data;
    }

    protected CompanySiteMetadata extractSiteMetadata(CompanyDTO company) throws IOException {
        String pageUrl = this.company.getVacanciesURL().replace("{page}","1");
        Map<String, String> siteData = this.company.getData().entrySet().stream()
                .filter((entry) -> !isValueBinding(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, String> siteHeaders = this.company.getHeaders().entrySet().stream()
                .filter((entry) -> !isValueBinding(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        PageConnectionParams pageConnectionParams = PageConnectionParams.builder()
                .pageUrl(pageUrl)
                .headers(siteHeaders)
                .data(siteData)
                .build();

        PageParserResponse pageResponse = defaultPageParser.parsePage(pageConnectionParams);
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
    protected int setIntervalBetweenPagesSec() {
        return 1;
    }

    /**
     * Generate {@link PageConnectionParams} params for connection to a page
     * @param pageID ID of the page
     * @return {@link PageConnectionParams} page connection params object
     */
    protected abstract PageConnectionParams generatePageConnectionParams(int pageID, CompanyDTO company);

    /**
     * Parse page with {@link PageConnectionParams}. Returns  {@link PageDTO} object with metadata that represents the page
     * @param pageConnectionParams params for page connection
     * @return {@link PageDTO} page object
     */
    protected abstract PageDTO parsePage(PageConnectionParams pageConnectionParams) throws IOException;

    /**
     * Extracts blocks with vacancy from the page
     * @param page page
     * @return blocks of {@link Element} pages with vacancies
     */
    protected abstract List<Element> extractVacancyElementsFromPage(Document page);
    /**
     * Generates {@link VacancyDTO} object from block source
     * @param element vacancy block
     * @return vacancy object
     */
    protected abstract VacancyDTO generateVacancyFromElement(Element element);

}
