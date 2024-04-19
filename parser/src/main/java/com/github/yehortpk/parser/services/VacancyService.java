package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.models.VacancyDTO;
import com.github.yehortpk.parser.domain.parsers.site.SiteParser;
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

/**
 * This class provide methods for parsing and identifying outdated/new vacancies
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VacancyService {
    private final ApplicationContext applicationContext;
    private final CompanyService companyService;

    /**
     * Parse vacancies from all companies returned by {@link CompanyService}
     * @return parsed vacancies
     */
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
            SiteParser siteParser = (SiteParser) applicationContext.getBean(beanClass);
            siteParser.setCompany(companyDTO);

            Future<Set<VacancyDTO>> future = executor.submit(siteParser::parseAllVacancies);
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

    /**
     * Calculate difference between parsed and persistent vacancies
     * @param allVacancies - total vacancies set
     * @param persistedVacancies - persistent vacancies set
     * @return set of new vacancies
     */
    public Set<VacancyDTO> calculateNewVacancies(Set<VacancyDTO> allVacancies, Set<VacancyDTO> persistedVacancies) {
        Set<VacancyDTO> result = new HashSet<>(allVacancies);
        result.removeAll(persistedVacancies);

        return result;
    }

    /**
     * Calculate difference between persistent and parsed vacancies
     * @param allVacancies - total vacancies set
     * @param persistedVacancies - persistent vacancies set
     * @return set of outdated vacancies
     */
    public Set<VacancyDTO> calculateOutdatedVacancies(Set<VacancyDTO> allVacancies, Set<VacancyDTO> persistedVacancies) {
        Set<VacancyDTO> result = new HashSet<>(persistedVacancies);
        result.removeAll(allVacancies);

        return result;
    }
}
