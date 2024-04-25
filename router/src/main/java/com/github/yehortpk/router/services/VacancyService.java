package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.company.Company;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import com.github.yehortpk.router.repositories.VacancyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This service provides methods for control vacancies
 */
@Service
@RequiredArgsConstructor
public class VacancyService {
    final private VacancyRepository vacancyRepository;

    /**
     * Persists all the vacancies from the input list
     * @param vacancies vacancies to persisting
     */
    @Transactional
    public void addVacancies(List<VacancyDTO> vacancies) {
        List<Vacancy> vacancyEntities = new ArrayList<>();
        for (VacancyDTO vacancy : vacancies) {
            vacancyEntities.add(
                    Vacancy.builder()
                            .company(Company.builder().companyId(vacancy.getCompanyID()).build())
                            .vacancyId(vacancy.getVacancyID())
                            .title(vacancy.getTitle())
                            .minSalary(vacancy.getMinSalary())
                            .maxSalary(vacancy.getMaxSalary())
                            .link(vacancy.getLink())
                            .build()
            );
        }
        vacancyRepository.saveAll(vacancyEntities);
    }

    public List<Vacancy> getAllVacancies() {
        return vacancyRepository.findAll();
    }
}
