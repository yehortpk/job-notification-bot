package com.github.yehortpk.notifier.services;

import com.github.yehortpk.notifier.entities.CompanySiteInterface;
import com.github.yehortpk.notifier.models.CompanyDTO;
import com.github.yehortpk.notifier.models.VacancyDAO;
import com.github.yehortpk.notifier.models.VacancyDTO;
import com.github.yehortpk.notifier.repositories.VacancyRepository;
import org.springframework.beans.BeansException;
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
import java.util.stream.Collectors;

@Service
public class VacancyService {
    @Autowired
    private VacancyRepository vacancyRepository;

    @Autowired
    ApplicationContext applicationContext;

    public Set<VacancyDAO> getPersistedVacancies() {
        List<VacancyDAO> vacanciesList = vacancyRepository.findAll();

        return new HashSet<>(vacanciesList);
    }

    public Set<VacancyDTO> parseAllVacancies(List<CompanyDTO> companies) {
        List<Future<Set<VacancyDTO>>> futures = new ArrayList<>();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(companies.size());
        for (CompanyDTO companyDTO : companies) {
            Future<Set<VacancyDTO>> future = executor.submit(() -> {
                try {
                    String beanClass = companyDTO.getBeanClass();
                    CompanySiteInterface companyBean = (CompanySiteInterface) applicationContext.getBean(beanClass);
                    companyBean.setCompany(companyDTO);
                    return companyBean.parseAllVacancies();
                } catch (BeansException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }

            });
            futures.add(future);

        }
        Set<VacancyDTO> vacancies = new HashSet<>();
        for (Future<Set<VacancyDTO>> future : futures) {
            try {
                Set<VacancyDTO> companyVacancies = future.get();
                vacancies.addAll(companyVacancies);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        executor.shutdown();
        return vacancies;
    }

    public Set<VacancyDTO> getDifference(Set<VacancyDTO> allVacancies, Set<VacancyDTO> persistedVacancies) {
        Set<VacancyDTO> result = new HashSet<>(allVacancies);
        result.removeAll(persistedVacancies);

        return result;
    }

    public Set<VacancyDTO> getOutdatedVacancies(Set<VacancyDTO> allVacancies, Set<VacancyDTO> persistedVacancies) {
        Set<VacancyDTO> result = new HashSet<>(persistedVacancies);
        result.removeAll(allVacancies);

        return result;
    }

    public void removeVacancies(Set<VacancyDTO> outdatedVacancies) {
        Set<VacancyDAO> vacancies = outdatedVacancies.stream().map(VacancyDTO::toDAO).collect(Collectors.toSet());
        vacancyRepository.deleteAll(vacancies);
    }

    public void addVacancies(Set<VacancyDTO> newVacancies) {
        Set<VacancyDAO> vacancies = newVacancies.stream().map(VacancyDTO::toDAO).collect(Collectors.toSet());
        vacancyRepository.saveAll(vacancies);
    }
}
