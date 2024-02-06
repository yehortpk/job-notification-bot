package com.github.yehortpk.notifier;

import com.github.yehortpk.notifier.entities.CompanySiteInterface;
import com.github.yehortpk.notifier.models.CompanyDTO;
import com.github.yehortpk.notifier.models.VacancyDTO;
import com.github.yehortpk.notifier.repositories.CompanyRepository;
import com.github.yehortpk.notifier.services.NotifierService;
import com.github.yehortpk.notifier.services.ProxyService;
import com.github.yehortpk.notifier.services.VacancyService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Profile("debug")
@Component
public class NotifierRunner implements ApplicationRunner {
    @Autowired
    VacancyService vacancyService;

    @Autowired
    ProxyService proxyService;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    NotifierService notifierService;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws IOException {
        proxyService.loadProxies();

        List<CompanyDTO> companies = companyRepository.findByIsEnabledTrue()
                .stream()
                .map(CompanyDTO::fromDAO)
                .toList();

        CompanySiteInterface companyBean;
        for (CompanyDTO companyDTO : companies) {
            try{
                String beanClass = companyDTO.getBeanClass();
                companyBean = (CompanySiteInterface) applicationContext.getBean(beanClass);
                companyBean.setCompany(companyDTO);
            } catch (BeansException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }

            Set<VacancyDTO> parsedVacancies = companyBean.parseAllVacancies();
            Set<VacancyDTO> persistedVacancies = vacancyService.getPersistedVacancies()
                    .stream()
                    .map(VacancyDTO::fromDAO)
                    .collect(Collectors.toSet());

            System.out.println("Persisted vacancies count: " + persistedVacancies.size());

            Set<VacancyDTO> newVacancies = vacancyService.getDifference(parsedVacancies, persistedVacancies);
            Set<VacancyDTO> outdatedVacancies = vacancyService.getOutdatedVacancies(parsedVacancies, persistedVacancies);
            System.out.println("new vacancies count: " + newVacancies.size());
            System.out.println("outdated vacancies count: " + outdatedVacancies.size());

            vacancyService.removeVacancies(outdatedVacancies);
            vacancyService.addVacancies(newVacancies);

            if (!newVacancies.isEmpty()) {
                System.out.println("New vacancies:");
                newVacancies.forEach(System.out::println);
                notifierService.notifyNewVacancies(newVacancies);
            }
        }
    }
}

