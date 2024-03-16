package com.github.yehortpk.notifier.services;

import com.github.yehortpk.notifier.entities.companies.CompanySiteImpl;
import com.github.yehortpk.notifier.models.CompanyDTO;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.springframework.beans.factory.annotation.Autowired;
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
public class VacancyService {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CompanyService companyService;

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
            CompanySiteImpl bean = (CompanySiteImpl) applicationContext.getBean(beanClass);

            bean.setCompany(companyDTO);
            Future<Set<VacancyDTO>> future = executor.submit(bean::parseAllVacancies);
            futures.add(future);
        }
        for (Future<Set<VacancyDTO>> future : futures) {
            try {
                Set<VacancyDTO> companyVacancies = future.get();
                vacancies.addAll(companyVacancies);
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(e.getMessage());
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
