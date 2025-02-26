package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.exceptions.NoVacanciesOnPageException;
import com.github.yehortpk.parser.models.PageDTO;
import com.github.yehortpk.parser.parser.SiteParser;
import com.github.yehortpk.parser.exceptions.ParsingAlreadyStartedException;
import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.progress.ParserProgress;
import com.github.yehortpk.parser.models.VacancyDTO;
import com.github.yehortpk.parser.progress.ParsingProgressService;
import com.github.yehortpk.parser.scrapper.site.SiteScrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

import static com.github.yehortpk.parser.utils.LogUtils.createErrorMessage;

/**
 * This class implements the {@link ApplicationRunner} interface and is responsible for parsing vacancies,
 * identifying new and outdated ones, and notifying about new vacancies.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ParserRunnerService {
    private final ApplicationContext applicationContext;
    private final CompanyService companyService;
    private final NotifierService notifierService;
    private final ParsingProgressService parsingProgressService;
    private Map<Long, Set<String>> persistedVacanciesByCompanyId;

    private Thread runnerThread;

    private void runParsers() {
        persistedVacanciesByCompanyId = companyService.getPersistedVacanciesUrlsByCompanyId();
        List<CompanyDTO> companies = companyService.getCompaniesList();

        if (companies.isEmpty()) {
            return;
        }

        ExecutorService executor = Executors.newCachedThreadPool();
        List<CompletableFuture<Void>> parsedVacanciesListFut = new ArrayList<>();

        for (CompanyDTO company : companies) {
            String beanClass = company.getBeanClass();
            SiteScrapper siteScrapper;
            try {
                siteScrapper = (SiteScrapper) applicationContext.getBean(beanClass);
            } catch (BeansException ignored) {
                log.error("{} scrapper implementation doesn't exist", beanClass);
                continue;
            }

            parsingProgressService.addParserProgress(company.getCompanyId(), company.getTitle());

            CompletableFuture<List<CompletableFuture<PageDTO>>> companyPageScrappersFut =
                    CompletableFuture.supplyAsync(() -> siteScrapper.scrapCompanyVacancies(company), executor)
                            .exceptionally(ex -> {
                                handleCompanyScrapperException(ex, company);
                                return List.of();
                            });

            CompletableFuture<Void> parsedVacanciesFut = companyPageScrappersFut.thenCompose(pages ->
                    CompletableFuture.allOf(pages.stream()
                            .map(scrapPageFut -> scrapPageFut
                                    .exceptionally(ex -> {
                                        int pageID = pages.indexOf(scrapPageFut) + 1;
                                        handlePageScrapperException(ex, company, pageID);
                                        return null;
                                    })
                                    .thenCompose(page -> {
                                        if (page == null) {
                                            return CompletableFuture.completedFuture(null);
                                        }
                                        return CompletableFuture.runAsync(
                                                        () -> parsePageVacancies(page, company), executor)
                                                .exceptionally(ex -> {
                                                    handlePageParserException(ex, company, pages.indexOf(scrapPageFut) + 1);
                                                    return null;
                                                });
                                    })
                            )
                            .toArray(CompletableFuture[]::new)
                    )
            );

            parsedVacanciesListFut.add(parsedVacanciesFut);
        }

        CompletableFuture.allOf(parsedVacanciesListFut.toArray(new CompletableFuture[0])).join();
        executor.shutdown();


        parsingProgressService.setFinished(true);

        String parsingResultOutput = String.format("""
                Parsing completed.\s
                Total vacancies parsed: %s.\s
                New vacancies count: %s""",
                parsingProgressService.getParsedVacanciesCnt(),
                parsingProgressService.getNewVacanciesCnt());
        log.info(parsingResultOutput);

        notifierService.notifyFinishedProgress(parsingProgressService.getProgress());
    }

    private void handleCompanyScrapperException(Throwable ex, CompanyDTO company) {
        ParserProgress parserProgress = parsingProgressService.getParsers().get(company.getCompanyId());
        String errorLog = String.format("Company: %s, error: %s",
                company.getTitle(), createErrorMessage((Exception) ex));

        log.error(errorLog);
        parserProgress.markPageError(1);
        parserProgress.addPageLog(1, ParserProgress.LogLevelEnum.ERROR, errorLog);
    }

    private void handlePageScrapperException(Throwable ex, CompanyDTO company, int pageID) {
        ParserProgress parserProgress = parsingProgressService.getParsers().get(company.getCompanyId());
        String errorLog = String.format("Company: %s, pageID:%s, error: %s",
                company.getTitle(), pageID, createErrorMessage((Exception) ex));
        log.error(errorLog);
        parserProgress.markPageError(pageID);
        parserProgress.addPageLog(pageID, ParserProgress.LogLevelEnum.ERROR, errorLog);
    }

    private void handlePageParserException(Throwable ex, CompanyDTO company, int pageID) {
        ParserProgress parserProgress = parsingProgressService.getParsers().get(company.getCompanyId());
        String errorLog = String.format("Company: %s, page: %s error: %s",
                company.getTitle(), pageID, createErrorMessage((Exception) ex));
        log.error(errorLog);
        parserProgress.markPageError(pageID);
        parserProgress.addPageLog(pageID, ParserProgress.LogLevelEnum.ERROR, errorLog);
    }

    private void parsePageVacancies(PageDTO page, CompanyDTO company) {
        SiteParser siteParser = (SiteParser) applicationContext.getBean(company.getBeanClass());
        ParserProgress parserProgress = parsingProgressService.getParsers().get(company.getCompanyId());

        int pageID = page.getPageID();

        Set<VacancyDTO> parsedVacancies;
        try {
            parsedVacancies = siteParser.parseVacancies(page);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (parsedVacancies.isEmpty()) {
            throw new NoVacanciesOnPageException(page.getPageID());
        }

        parsedVacancies.forEach(vacancy -> {
            vacancy.setCompanyID(company.getCompanyId());
            vacancy.setCompanyTitle(company.getTitle());
        });

        Set<String> persistedCompanyVacancies =
                persistedVacanciesByCompanyId.getOrDefault((long) company.getCompanyId(), new HashSet<>());

        ParserProgress.PageProgress pageProgress = parserProgress.getPages().get(pageID - 1);

        Set<VacancyDTO> newVacancies = calculateNewVacancies(parsedVacancies, persistedCompanyVacancies);

        int parsedVacanciesCnt = parsedVacancies.size();
        int newVacanciesCnt = newVacancies.size();

        parserProgress.markPageDone(pageID);

        String successLog = String.format("Company: %s, page: %s was parsed, parsed vacancies count = %s, new vacancies count = %s",
                company.getTitle(), pageID, parsedVacanciesCnt, newVacanciesCnt);
        log.info(successLog);
        parserProgress.addPageLog(pageID, ParserProgress.LogLevelEnum.INFO, successLog);

        pageProgress.setParsedVacanciesCnt(parsedVacanciesCnt);
        pageProgress.setNewVacanciesCnt(newVacanciesCnt);

        parserProgress.addParsedVacancies(parsedVacanciesCnt);
        parserProgress.addNewVacancies(newVacanciesCnt);

        parsingProgressService.addParsedVacancies(parsedVacanciesCnt);
        parsingProgressService.addNewVacancies(newVacanciesCnt);

        notifierService.notifyNewVacancies(newVacancies);
    }

    public void runParsing() throws ParsingAlreadyStartedException {
        if (runnerThread == null || !runnerThread.isAlive()) {
            parsingProgressService.init();
            runnerThread = new Thread(this::runParsers);
            runnerThread.start();
        } else {
            throw new ParsingAlreadyStartedException("Parsing process has already started");
        }
    }

    /**
     * Calculate difference between parsed and persistent vacancies
     * @param parsedVacancies - total vacancies set
     * @param persistedVacanciesUrls - persistent vacancies set
     * @return set of new vacancies
     */
    public synchronized Set<VacancyDTO> calculateNewVacancies(Set<VacancyDTO> parsedVacancies, Set<String> persistedVacanciesUrls) {
        Set<VacancyDTO> result = new HashSet<>(parsedVacancies);
        result.removeIf(vacancyDTO -> persistedVacanciesUrls.contains(vacancyDTO.getLink()));
        return result;
    }
}

