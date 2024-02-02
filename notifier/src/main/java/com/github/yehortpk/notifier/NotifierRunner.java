package com.github.yehortpk.notifier;

import com.github.yehortpk.notifier.entities.SomeCompany;
import com.github.yehortpk.notifier.models.VacancyDTO;
import com.github.yehortpk.notifier.services.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import com.github.yehortpk.notifier.services.VacancyService;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NotifierRunner implements ApplicationRunner {
    @Autowired
    VacancyService vacancyService;

    @Autowired
    ProxyService proxyService;

    @Autowired
    SomeCompany someCompany;

    @Override
    public void run(ApplicationArguments args) throws IOException {
        proxyService.loadProxies();
        Set<VacancyDTO> parsedVacancies = someCompany.parseAllVacancies();
        Set<VacancyDTO> persistedVacancies = vacancyService.getPersistedVacancies()
                .stream()
                .map(VacancyDTO::fromDAO)
                .collect(Collectors.toSet());

        Set<VacancyDTO> newVacancies = vacancyService.getDifference(parsedVacancies, persistedVacancies);
        Set<VacancyDTO> outdatedVacancies = vacancyService.getOutdatedVacancies(parsedVacancies, persistedVacancies);

        vacancyService.removeVacancies(outdatedVacancies);
        vacancyService.addVacancies(newVacancies);

        System.out.println("New vacancies:");
        newVacancies.forEach(System.out::println);
    }
}