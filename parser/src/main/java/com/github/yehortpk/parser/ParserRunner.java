package com.github.yehortpk.parser;

import com.github.yehortpk.parser.models.VacancyDTO;
import com.github.yehortpk.parser.services.CompanyService;
import com.github.yehortpk.parser.services.NotifierService;
import com.github.yehortpk.parser.services.VacancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
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
public class ParserRunner implements ApplicationRunner {
    private final VacancyService vacancyService;
    private final CompanyService companyService;
    private final NotifierService notifierService;
    private final ConfigurableApplicationContext context;

    @Override
    public void run (ApplicationArguments args) {
        Set<VacancyDTO> parsedVacancies = vacancyService.parseAllVacancies();
        Set<VacancyDTO> newVacancies = new HashSet<>();
        Set<VacancyDTO> outdatedVacancies = new HashSet<>();

        Map<Integer, List<VacancyDTO>> vacanciesByCompany = parsedVacancies.stream()
                .collect(Collectors.groupingBy(VacancyDTO::getCompanyID));

        vacanciesByCompany.forEach((companyId, vacancies) -> {
            List<VacancyDTO> persistedCompanyVacancies = companyService.getPersistedVacancies(companyId);
            Set<VacancyDTO> persistedCompanyVacanciesSet = new HashSet<>(persistedCompanyVacancies);
            Set<VacancyDTO> parsedCompanyVacanciesSet = new HashSet<>(vacancies);
            newVacancies.addAll(vacancyService.calculateNewVacancies(parsedCompanyVacanciesSet, persistedCompanyVacanciesSet));
            if (!parsedCompanyVacanciesSet.isEmpty()) {
                outdatedVacancies.addAll(
                        vacancyService.calculateOutdatedVacancies(parsedCompanyVacanciesSet, persistedCompanyVacanciesSet));
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

        context.close();
    }
}

