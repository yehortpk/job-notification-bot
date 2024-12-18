package com.github.yehortpk.parser;

import com.github.yehortpk.parser.exceptions.ParsingAlreadyStartedException;
import com.github.yehortpk.parser.models.VacancyDTO;
import com.github.yehortpk.parser.services.CompanyService;
import com.github.yehortpk.parser.services.NotifierService;
import com.github.yehortpk.parser.services.ProgressManagerService;
import com.github.yehortpk.parser.services.VacancyService;
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
            Set<String> outdatedVacancies = new HashSet<>();

            Map<Integer, List<VacancyDTO>> vacanciesByCompany = parsedVacancies.stream()
                    .collect(Collectors.groupingBy(VacancyDTO::getCompanyID));

            Map<Long, Set<String>> persistedVacanciesByCompanyId = companyService.getPersistedVacanciesUrlsByCompanyId();

            vacanciesByCompany.forEach((companyId, vacancies) -> {
                Set<String> persistedCompanyVacancies = persistedVacanciesByCompanyId.get((long) companyId);
                if (persistedCompanyVacancies == null) {
                    persistedCompanyVacancies = new HashSet<>();
                }
                Set<VacancyDTO> parsedCompanyVacanciesSet = new HashSet<>(vacancies);
                newVacancies.addAll(vacancyService.calculateNewVacancies(parsedCompanyVacanciesSet, persistedCompanyVacancies));
            });

            progressManagerService.setFinished(true);
            progressManagerService.setParsedVacanciesCnt(parsedVacancies.size());
            progressManagerService.setNewVacanciesCnt(newVacancies.size());
            progressManagerService.setOutdatedVacanciesCnt(outdatedVacancies.size());

            String parsingResultOutput = String.format("""
                    Parsing completed.
                    New vacancies count: %s
                    outdated vacancies\
                     count: %s""", newVacancies.size(), outdatedVacancies.size());
            System.out.println(parsingResultOutput);
            log.info(parsingResultOutput);

            if (!newVacancies.isEmpty()) {
                notifierService.notifyNewVacancies(newVacancies);
            }

            if (!outdatedVacancies.isEmpty()) {
                notifierService.notifyOutdatedVacancies(outdatedVacancies);
            }
        };
    }

    public void runParsers() throws ParsingAlreadyStartedException {
        if (runnerThread == null || !runnerThread.isAlive()) {
            runnerThread = new Thread(run());
            runnerThread.start();
        } else {
            throw new ParsingAlreadyStartedException("Parsing process has already started");
        }
    }
}

