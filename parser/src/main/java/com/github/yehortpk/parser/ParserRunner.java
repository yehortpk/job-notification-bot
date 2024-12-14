package com.github.yehortpk.parser;

import com.github.yehortpk.parser.models.VacancyDTO;
import com.github.yehortpk.parser.services.CompanyService;
import com.github.yehortpk.parser.services.NotifierService;
import com.github.yehortpk.parser.services.VacancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class implements the {@link ApplicationRunner} interface and is responsible for parsing vacancies,
 * identifying new and outdated ones, and notifying about new vacancies.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ParserRunner extends Thread{
    private final VacancyService vacancyService;
    private final CompanyService companyService;
    private final NotifierService notifierService;

    @Override
    public void run () {
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
            if (!parsedCompanyVacanciesSet.isEmpty()) {
                outdatedVacancies.addAll(
                        vacancyService.calculateOutdatedVacanciesIds(parsedCompanyVacanciesSet, persistedCompanyVacancies));
            }
        });

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
    }
}

