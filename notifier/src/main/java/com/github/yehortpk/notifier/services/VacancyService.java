package com.github.yehortpk.notifier.services;

import com.github.yehortpk.notifier.models.CompanyDTO;
import com.github.yehortpk.notifier.models.VacancyDTO;
import com.github.yehortpk.notifier.parsers.site.SiteParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@RequiredArgsConstructor
@Slf4j
public class VacancyService {
    private final ApplicationContext applicationContext;
    private final CompanyService companyService;

    public Set<VacancyDTO> parseAllVacancies() {
        List<CompanyDTO> companies = companyService.getCompaniesList();

        if (companies.isEmpty()) {
            return new HashSet<>();
        }

        Set<VacancyDTO> vacancies = new HashSet<>();
        List<Future<Set<VacancyDTO>>> futures = new ArrayList<>();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(companies.size());
        for (CompanyDTO companyDTO : companies) {
            String beanClass = companyDTO.getBeanClass();
            SiteParser bean = (SiteParser) applicationContext.getBean(beanClass);
            bean.setCompany(companyDTO);

            Future<Set<VacancyDTO>> future = executor.submit(bean::parseAllVacancies);
            futures.add(future);
        }
        for (Future<Set<VacancyDTO>> future : futures) {
            try {
                Set<VacancyDTO> companyVacancies = future.get();
                vacancies.addAll(companyVacancies);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Can't parse page, {}", e.getMessage());
            }
        }

        executor.close();
        return vacancies;
    }

    public Set<VacancyDTO> getNewVacancies(Set<VacancyDTO> allVacancies, Set<VacancyDTO> persistedVacancies) {
        Set<VacancyDTO> result = new HashSet<>(allVacancies);
        result.removeAll(persistedVacancies);

        return result;
    }

    public Set<VacancyDTO> getOutdatedVacancies(Set<VacancyDTO> allVacancies, Set<VacancyDTO> persistedVacancies) {
        Set<VacancyDTO> result = new HashSet<>(persistedVacancies);
        result.removeAll(allVacancies);

        return result;
    }
}
