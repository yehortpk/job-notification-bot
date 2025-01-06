package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.exceptions.ParsingAlreadyStartedException;
import com.github.yehortpk.parser.models.ParserProgress;
import com.github.yehortpk.parser.models.VacancyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class implements the {@link ApplicationRunner} interface and is responsible for parsing vacancies,
 * identifying new and outdated ones, and notifying about new vacancies.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ParserRunnerService {
    private final VacancyService vacancyService;
    private final CompanyService companyService;
    private final NotifierService notifierService;
    private final ProgressManagerService progressManagerService;
    private Thread runnerThread;

    private Runnable run () {
        return () -> {
            Set<VacancyDTO> parsedVacancies = vacancyService.parseAllVacancies();
            Set<VacancyDTO> newVacancies = new HashSet<>();

            Map<Integer, List<VacancyDTO>> vacanciesByCompany = parsedVacancies.stream()
                    .collect(Collectors.groupingBy(VacancyDTO::getCompanyID));

            Map<Long, Set<String>> persistedVacanciesByCompanyId = companyService.getPersistedVacanciesUrlsByCompanyId();

            vacanciesByCompany.forEach((companyId, vacancies) -> {
                Set<String> persistedCompanyVacancies = persistedVacanciesByCompanyId.get((long) companyId);
                if (persistedCompanyVacancies == null) {
                    persistedCompanyVacancies = new HashSet<>();
                }
                Set<VacancyDTO> parsedCompanyVacanciesSet = new HashSet<>(vacancies);
                Set<VacancyDTO> companyNewVacancies = vacancyService.calculateNewVacancies(parsedCompanyVacanciesSet, persistedCompanyVacancies);
                ParserProgress parserProgress = progressManagerService.getParsers().get(companyId);
                parserProgress.setParsedVacanciesCnt(vacancies.size());
                parserProgress.setNewVacanciesCnt(companyNewVacancies.size());

                newVacancies.addAll(companyNewVacancies);
            });

            progressManagerService.setFinished(true);
            progressManagerService.setParsedVacanciesCnt(parsedVacancies.size());
            progressManagerService.setNewVacanciesCnt(newVacancies.size());

            String parsingResultOutput = String.format("""
                    Parsing completed.\s
                    Total vacancies parsed: %s.\s
                    New vacancies count: %s""", parsedVacancies.size(), newVacancies.size());
            System.out.println(parsingResultOutput);
            log.info(parsingResultOutput);

            if (!newVacancies.isEmpty()) {
                notifierService.notifyNewVacancies(newVacancies);
            }
        };
    }

    public void runParsers() throws ParsingAlreadyStartedException {
        if (runnerThread == null || !runnerThread.isAlive()) {
            progressManagerService.initialize();
            runnerThread = new Thread(run());
            runnerThread.start();
        } else {
            throw new ParsingAlreadyStartedException("Parsing process has already started");
        }
    }
}

