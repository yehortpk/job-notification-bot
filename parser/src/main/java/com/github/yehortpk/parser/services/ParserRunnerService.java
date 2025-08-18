package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.exceptions.NoVacanciesOnPageException;
import com.github.yehortpk.parser.models.PageDTO;
import com.github.yehortpk.parser.parser.SiteParser;
import com.github.yehortpk.parser.exceptions.ParsingAlreadyStartedException;
import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.progress.ParserProgress;
import com.github.yehortpk.parser.models.VacancyDTO;
import com.github.yehortpk.parser.scrapper.site.SiteScrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final ParsingHistoryService parsingHistoryService;
    private final VacancyService vacancyService;

    private final Map<Integer, List<VacancyDTO>> persistedVacanciesByCompany = new HashMap<>();

    private Thread runnerThread;

    private void runParsers() {
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

            int companyId = company.getCompanyId();
            List<VacancyDTO> persistedVacancies = companyService.getPersistedVacancies(companyId);
            persistedVacanciesByCompany.put(companyId, persistedVacancies);
            parsingProgressService.addParserProgress(companyId, company.getTitle());

            CompletableFuture<List<CompletableFuture<PageDTO>>> companyPageScrappersFut =
                    CompletableFuture.supplyAsync(() -> siteScrapper.scrapCompanyVacancies(company), executor)
                            .exceptionally(ex -> {
                                handleCompanyScrapperException(ex, company);
                                return List.of();
                            });

            CompletableFuture<Void> parsedVacanciesFut = companyPageScrappersFut.thenCompose(pages ->
                    CompletableFuture.supplyAsync(() -> {
                        List<CompletableFuture<List<VacancyDTO>>> vacancyFutures = pages.stream()
                                .map(scrapPageFut -> scrapPageFut
                                        .exceptionally(ex -> {
                                            int pageID = pages.indexOf(scrapPageFut) + 1;
                                            handlePageScrapperException(ex, company, pageID);
                                            return null;
                                        })
                                        .thenCompose(page -> {
                                            if (page == null) {
                                                return CompletableFuture.completedFuture(List.of());
                                            }
                                            return CompletableFuture.supplyAsync(
                                                            () -> parsePageVacancies(page, company), executor)
                                                    .exceptionally(ex -> {
                                                        handlePageParserException(ex, company, pages.indexOf(scrapPageFut) + 1);
                                                        return List.of(); // Return empty list on exception
                                                    });
                                        })
                                )
                                .toList();

                        // Wait for all futures to complete and collect results
                        List<VacancyDTO> parsedVacancies = vacancyFutures.stream()
                                .map(CompletableFuture::join)
                                .flatMap(List::stream)
                                .toList();


                        handleOutdatedVacancies(parsedVacancies, persistedVacancies);

                        return null;
                    }, executor)
            );

            parsedVacanciesListFut.add(parsedVacanciesFut);
        }

        CompletableFuture.allOf(parsedVacanciesListFut.toArray(new CompletableFuture[0])).join();
        executor.shutdown();


        parsingProgressService.setFinished(true);
        parsingHistoryService.saveProgress(parsingProgressService.getProgress());

        String parsingResultOutput = String.format("""
                Parsing completed.\s
                Total vacancies parsed: %s\s
                New vacancies count: %s\s
                Outdated vacancies count: %s
                """,
                parsingProgressService.getParsedVacanciesCnt(),
                parsingProgressService.getNewVacanciesCnt(),
                parsingProgressService.getOutdatedVacanciesCnt()
        );
        log.info(parsingResultOutput);
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

    private void handleOutdatedVacancies(List<VacancyDTO> parsedVacancies, List<VacancyDTO> persistedVacancies) {
        if (persistedVacancies.isEmpty()) {
            return;
        }

        int companyID = persistedVacancies.getFirst().getCompanyID();

        Set<VacancyDTO> outdatedVacancies = calculateOutdatedVacancies(parsedVacancies, persistedVacancies);
        List<VacancyDTO> vacanciesOnDeletion =
                persistedVacancies.stream().filter(vacancy -> vacancy.getDeleteAt() != null).toList();

        for (VacancyDTO vacancyOnDeletion : vacanciesOnDeletion) {
            outdatedVacancies.remove(vacancyOnDeletion);
            // In case of an outdated vacancy actually was marked outdated by a mistake
            // (e.g. page wasn't parsed) we remove deletion marker
            if (parsedVacancies.contains(vacancyOnDeletion)) {
                vacancyOnDeletion.setDeleteAt(null);
                vacancyService.updateVacancy(vacancyOnDeletion);
                // In case of an outdated vacancy period is expired - remove it
            } else if (vacancyOnDeletion.getDeleteAt().isBefore(LocalDateTime.now())) {
                vacancyService.deleteVacancy(vacancyOnDeletion.getVacancyID());
            }
        }

        // In case of newly outdated vacancy set expiration timeout of one week, after which,
        // if vacancy still wasn't parsed - remove it
        for (VacancyDTO outdatedVacancy : outdatedVacancies) {
            outdatedVacancy.setDeleteAt(LocalDateTime.now().plusWeeks(1));
            vacancyService.updateVacancy(outdatedVacancy);
        }

        int outdatedVacanciesCnt = outdatedVacancies.size();
        parsingProgressService.getParsers().get(companyID).setOutdatedVacanciesCnt(outdatedVacanciesCnt);
        parsingProgressService.addOutdatedVacancies(outdatedVacanciesCnt);
    }

    private List<VacancyDTO> parsePageVacancies(PageDTO page, CompanyDTO company) {
        SiteParser siteParser = (SiteParser) applicationContext.getBean(company.getBeanClass());
        ParserProgress parserProgress = parsingProgressService.getParsers().get(company.getCompanyId());

        int pageID = page.getPageID();

        List<VacancyDTO> parsedVacancies;
        try {
            parsedVacancies = siteParser.parseVacancies(page);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (parsedVacancies.isEmpty()) {
            throw new NoVacanciesOnPageException(page.getPageID());
        }

        for (VacancyDTO vacancy : parsedVacancies) {
            vacancy.setCompanyID(company.getCompanyId());
            vacancy.setCompanyTitle(company.getTitle());
            vacancy.setParsedAt(LocalDateTime.now());
        }

        List<VacancyDTO> persistedCompanyVacancies =
                persistedVacanciesByCompany.getOrDefault(company.getCompanyId(), new ArrayList<>());

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

        return parsedVacancies;
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
     * @param persistedVacancies - persistent vacancies set
     * @return set of new vacancies
     */
    private Set<VacancyDTO> calculateNewVacancies(List<VacancyDTO> parsedVacancies, List<VacancyDTO> persistedVacancies) {
        Set<VacancyDTO> parsedVacanciesSet = new HashSet<>(parsedVacancies);
        Set<VacancyDTO> persistedVacanciesSet = new HashSet<>(persistedVacancies);

        parsedVacanciesSet.removeAll(persistedVacanciesSet);

        return parsedVacanciesSet;
    }

    private Set<VacancyDTO> calculateOutdatedVacancies(List<VacancyDTO> parsedVacancies, List<VacancyDTO> persistedVacancies) {
        Set<VacancyDTO> parsedVacanciesSet = new HashSet<>(parsedVacancies);
        Set<VacancyDTO> persistedVacanciesSet = new HashSet<>(persistedVacancies);

        persistedVacanciesSet.removeAll(parsedVacanciesSet);

        return persistedVacanciesSet;
    }


}

