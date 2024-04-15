package com.github.yehortpk.parser;

import com.github.yehortpk.parser.models.VacancyDTO;
import com.github.yehortpk.parser.services.CompanyService;
import com.github.yehortpk.parser.services.NotifierService;
import com.github.yehortpk.parser.services.VacancyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ParserRunner implements ApplicationRunner {
    @Autowired
    VacancyService vacancyService;

    @Autowired
    CompanyService companyService;

    @Autowired
    NotifierService notifierService;

    @Override
    public void run (ApplicationArguments args) {
        Set<VacancyDTO> parsedVacancies = vacancyService.parseAllVacancies();
        Set<VacancyDTO> newVacancies = new HashSet<>();
        Set<VacancyDTO> outdatedVacancies = new HashSet<>();

        Map<Integer, List<VacancyDTO>> vacanciesByCompany = parsedVacancies.stream()
                .collect(Collectors.groupingBy(VacancyDTO::getCompanyID));

        vacanciesByCompany.forEach((companyId, vacancies) -> {
            List<VacancyDTO> persistedCompanyVacancies = companyService.getVacancies(companyId);
            Set<VacancyDTO> persistedCompanyVacanciesSet = new HashSet<>(persistedCompanyVacancies);
            Set<VacancyDTO> parsedCompanyVacanciesSet = new HashSet<>(vacancies);
            newVacancies.addAll(vacancyService.getNewVacancies(parsedCompanyVacanciesSet, persistedCompanyVacanciesSet));
            outdatedVacancies.addAll(
                    vacancyService.getOutdatedVacancies(parsedCompanyVacanciesSet, persistedCompanyVacanciesSet));
        });

        log.info("new vacancies count: " + newVacancies.size());
        log.info("outdated vacancies count: " + outdatedVacancies.size());

        if (!newVacancies.isEmpty()) {
            log.info("New vacancies:");
            newVacancies.forEach((vacancy) -> log.info(vacancy.toString()));
            notifierService.notifyNewVacancies(newVacancies);
        }
    }
}

