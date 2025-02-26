package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.parser.SiteParser;
import com.github.yehortpk.parser.exceptions.ParsingAlreadyStartedException;
import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.progress.PageProgressStatusEnum;
import com.github.yehortpk.parser.progress.ParserProgress;
import com.github.yehortpk.parser.models.VacancyDTO;
import com.github.yehortpk.parser.progress.ProgressManagerService;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

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
    private final ProgressManagerService progressManagerService;
    private Thread runnerThread;

    private Runnable run () {
        return () -> {
            List<CompanyDTO> companies = companyService.getCompaniesList();

            if (companies.isEmpty()) {
                return ;
            }

            Map<CompanyDTO, CompletableFuture<Set<VacancyDTO>>> vacanciesByCompaniesFutures = new HashMap<>();
            @Cleanup ExecutorService executor = Executors.newCachedThreadPool();

            for (CompanyDTO company : companies) {
                String beanClass = company.getBeanClass();
                try {
                    SiteParser siteParser = (SiteParser) applicationContext.getBean(beanClass);

                    CompletableFuture<Set<VacancyDTO>> vacanciesByCompany =
                            CompletableFuture.supplyAsync(() -> siteParser.parseVacancies(company), executor);
                    vacanciesByCompaniesFutures.put(company, vacanciesByCompany);
                } catch (BeansException ignored) {
                    log.error("{} parser implementation doesn't exist", beanClass);
                }
            }

            Map<Long, Set<String>> persistedVacanciesByCompanyId = companyService.getPersistedVacanciesUrlsByCompanyId();

            for (Map.Entry<CompanyDTO, CompletableFuture<Set<VacancyDTO>>> vacanciesByCompanyEntry : vacanciesByCompaniesFutures.entrySet()) {
                CompanyDTO company = vacanciesByCompanyEntry.getKey();
                CompletableFuture<Set<VacancyDTO>> vacanciesFut = vacanciesByCompanyEntry.getValue();

                synchronized (this) {
                    vacanciesFut.whenComplete((parsedVacancies, error) -> {
                        if (error != null) {
                            throw new RuntimeException(error);
                        }

                        ParserProgress parserProgress = progressManagerService.getParsers().get(company.getCompanyId());
                        parserProgress.setParsedVacanciesCnt(parsedVacancies.size());

                        Set<String> persistedCompanyVacancies = persistedVacanciesByCompanyId.getOrDefault((long) company.getCompanyId(), new HashSet<>());
                        Set<VacancyDTO> companyNewVacancies = calculateNewVacancies(parsedVacancies, persistedCompanyVacancies);


                        parserProgress.setNewVacanciesCnt(companyNewVacancies.size());

                        progressManagerService.setParsedVacanciesCnt(progressManagerService.getParsedVacanciesCnt() + parsedVacancies.size());
                        progressManagerService.setNewVacanciesCnt(progressManagerService.getNewVacanciesCnt() + companyNewVacancies.size());
                        if (!companyNewVacancies.isEmpty()) {
                            notifierService.notifyNewVacancies(companyNewVacancies);
                        }

                        for (ParserProgress.PageProgress pPage : progressManagerService.getParsers().get(company.getCompanyId()).getPages()) {
                            if (pPage.getStatus().equals(PageProgressStatusEnum.STEP_ERROR)) {
                                return;
                            }
                        }

                        if (!parsedVacancies.isEmpty()) {
                            Set<String> companyOutdatedVacanciesIdentifiers = calculateOutdatedVacanciesIdentifiers(parsedVacancies, persistedCompanyVacancies);

                            if (!companyOutdatedVacanciesIdentifiers.isEmpty()) {
                                parserProgress.setOutdatedVacanciesCnt(companyOutdatedVacanciesIdentifiers.size());
                                log.info("Remove outdated vacancies from company {}, count: {}", company.getTitle(), companyOutdatedVacanciesIdentifiers.size());
                                notifierService.notifyOutdatedVacancies(companyOutdatedVacanciesIdentifiers);
                            }
                        }
                    });
                }
            }

            for (CompletableFuture<Set<VacancyDTO>> fut : vacanciesByCompaniesFutures.values()) {
                try {
                    fut.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            progressManagerService.setFinished(true);

            String parsingResultOutput = String.format("""
                    Parsing completed.\s
                    Total vacancies parsed: %s.\s
                    New vacancies count: %s""", progressManagerService.getParsedVacanciesCnt(), progressManagerService.getNewVacanciesCnt());
            log.info(parsingResultOutput);

            notifierService.notifyFinishedProgress(progressManagerService.getProgress());
        };
    private void handleCompanyScrapperException(Throwable ex, CompanyDTO company) {
        ParserProgress parserProgress = parsingProgressService.getParsers().get(company.getCompanyId());
        String errorLog = String.format("Company: %s, error: %s",
                company.getTitle(), createErrorMessage((Exception) ex));

        log.error(errorLog);
        parserProgress.markPageError(1);
        parserProgress.addPageLog(1, ParserProgress.LogLevelEnum.ERROR, errorLog);
    }

    public void runParsers() throws ParsingAlreadyStartedException {
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
            progressManagerService.init();
            runnerThread = new Thread(run());
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

    public synchronized Set<String> calculateOutdatedVacanciesIdentifiers(Set<VacancyDTO> parsedVacancies, Set<String> persistedVacanciesUrls) {
        for (VacancyDTO parsedVacancy : parsedVacancies) {
            persistedVacanciesUrls.remove(parsedVacancy.getLink());
        }

        return persistedVacanciesUrls;
    }
}

