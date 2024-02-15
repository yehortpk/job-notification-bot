package com.github.yehortpk.notifier;

import com.github.yehortpk.notifier.models.VacancyDTO;
import com.github.yehortpk.notifier.services.NotifierService;
import com.github.yehortpk.notifier.services.ProxyService;
import com.github.yehortpk.notifier.services.VacancyService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NotifierRunner {
    @Autowired
    ProxyService proxyService;

    @Autowired
    VacancyService vacancyService;

    @Autowired
    NotifierService notifierService;

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    @Transactional
    public void notifyNewVacancies () throws IOException {
        proxyService.loadProxies();

        Set<VacancyDTO> parsedVacancies = vacancyService.parseAllVacancies();

        Set<VacancyDTO> persistedVacancies = vacancyService.getPersistedVacancies()
                .stream()
                .map(VacancyDTO::fromDAO)
                .collect(Collectors.toSet());

        System.out.println("Persisted vacancies count: " + persistedVacancies.size());

        Set<VacancyDTO> newVacancies = vacancyService.getDifference(parsedVacancies, persistedVacancies);
        Set<VacancyDTO> outdatedVacancies = vacancyService.getOutdatedVacancies(parsedVacancies, persistedVacancies);
        System.out.println("new vacancies count: " + newVacancies.size());
        System.out.println("outdated vacancies count: " + outdatedVacancies.size());

        vacancyService.addVacancies(newVacancies);

        if (!newVacancies.isEmpty()) {
            System.out.println("New vacancies:");
            newVacancies.forEach(System.out::println);
            notifierService.notifyNewVacancies(newVacancies);
        }

        parsedVacancies.clear();
        persistedVacancies.clear();
        newVacancies.clear();
        outdatedVacancies.clear();
    }


}

