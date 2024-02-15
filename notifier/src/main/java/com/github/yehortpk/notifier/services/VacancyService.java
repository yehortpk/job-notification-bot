package com.github.yehortpk.notifier.services;

import com.github.yehortpk.notifier.entities.CompanySite;
import com.github.yehortpk.notifier.entities.MultiplePageCompanySite;
import com.github.yehortpk.notifier.entities.parsers.ThreadsPageParser;
import com.github.yehortpk.notifier.models.CompanyDTO;
import com.github.yehortpk.notifier.models.VacancyDAO;
import com.github.yehortpk.notifier.models.VacancyDTO;
import com.github.yehortpk.notifier.repositories.CompanyRepository;
import com.github.yehortpk.notifier.repositories.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;
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
    private CompanyRepository companyRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    ProxyService proxyService;

    public Set<VacancyDAO> getPersistedVacancies() {
        List<VacancyDAO> vacanciesList = vacancyRepository.findAll();

        return new HashSet<>(vacanciesList);
    }

    public Set<VacancyDTO> parseAllVacancies() {
        List<CompanyDTO> companies = companyRepository.findByIsEnabledTrue().stream().map(CompanyDTO::fromDAO).toList();

        List<Future<Set<VacancyDTO>>> futures = new ArrayList<>();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(companies.size());
        for (CompanyDTO companyDTO : companies) {
            String beanClass = companyDTO.getBeanClass();
            CompanySite bean = (CompanySite) applicationContext.getBean(beanClass);

            if (bean instanceof MultiplePageCompanySite multiPageBean) {
                multiPageBean.setCompany(companyDTO);

                Map<String, String> headers = multiPageBean.getHeaders();
                ThreadsPageParser pageParser = new ThreadsPageParser();
                pageParser.setHeaders(headers);
                pageParser.setProxyService(proxyService);
                multiPageBean.setPageParser(pageParser);
                Future<Set<VacancyDTO>> future = executor.submit(multiPageBean::parseAllVacancies);
                futures.add(future);
            }
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

        executor.close();
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

    public void addVacancies(Set<VacancyDTO> newVacancies) {
        Set<VacancyDAO> vacancies = newVacancies.stream().map(VacancyDTO::toDAO).collect(Collectors.toSet());
        vacancyRepository.saveAll(vacancies);
    }
}
