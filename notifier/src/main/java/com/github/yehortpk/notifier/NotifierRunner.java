package com.github.yehortpk.notifier;

import com.github.yehortpk.notifier.models.VacancyDTO;
import com.github.yehortpk.notifier.services.CompanyService;
import com.github.yehortpk.notifier.services.NotifierService;
import com.github.yehortpk.notifier.services.VacancyService;
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
            Set<VacancyDTO> persistedCompanyVacanciesSet = new HashSet<>(persistedCompanyVacancies);
            newVacancies.addAll(vacancyService.getNewVacancies(parsedVacancies, persistedCompanyVacanciesSet));
            outdatedVacancies.addAll(
                    vacancyService.getOutdatedVacancies(parsedVacancies, persistedCompanyVacanciesSet));
        });

        System.out.println("new vacancies count: " + newVacancies.size());
        System.out.println("outdated vacancies count: " + outdatedVacancies.size());

        if (!newVacancies.isEmpty()) {
            System.out.println("New vacancies:");
            newVacancies.forEach(System.out::println);
            notifierService.notifyNewVacancies(newVacancies);
        }
    }
}

