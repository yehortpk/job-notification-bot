package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.domain.parser.site.SiteParser;
import com.github.yehortpk.parser.exceptions.ParsingAlreadyStartedException;
import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.models.PageProgressStatusEnum;
import com.github.yehortpk.parser.models.ParserProgress;
import com.github.yehortpk.parser.models.VacancyDTO;
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
    }

    public void runParsers() throws ParsingAlreadyStartedException {
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

