package com.github.yehortpk.notifier;

import com.github.yehortpk.notifier.models.VacancyDTO;
import com.github.yehortpk.notifier.services.CompanyService;
import com.github.yehortpk.notifier.services.NotifierService;
import com.github.yehortpk.notifier.services.VacancyService;
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
public class NotifierRunner implements ApplicationRunner {
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
            log.debug("Persisted company={}, count={}", companyId, persistedCompanyVacancies.size());
            log.debug("parsed  count={}", vacancies.size());
            Set<VacancyDTO> persistedCompanyVacanciesSet = new HashSet<>(persistedCompanyVacancies);
            Set<VacancyDTO> parsedCompanyVacanciesSet = new HashSet<>(vacancies);
            newVacancies.addAll(vacancyService.getNewVacancies(parsedCompanyVacanciesSet, persistedCompanyVacanciesSet));
            outdatedVacancies.addAll(
                    vacancyService.getOutdatedVacancies(parsedCompanyVacanciesSet, persistedCompanyVacanciesSet));
        });

        log.debug("new vacancies count: " + newVacancies.size());
        log.debug("outdated vacancies count: " + outdatedVacancies.size());

        if (!newVacancies.isEmpty()) {
            log.debug("New vacancies:");
            newVacancies.forEach((vacancy) -> log.debug(vacancy.toString()));
            notifierService.notifyNewVacancies(newVacancies);
        }
    }
}

