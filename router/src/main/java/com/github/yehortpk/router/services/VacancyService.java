package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.company.Company;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import com.github.yehortpk.router.repositories.CompanyRepository;
import com.github.yehortpk.router.repositories.VacancyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service provides methods for control vacancies
 */
@Service
@RequiredArgsConstructor
public class VacancyService {
    final private VacancyRepository vacancyRepository;
    final private CompanyRepository companyRepository;

    /**
     * Persists all the vacancies from the input list
     * @param vacancies vacancies to persisting
     */
    @Transactional
    public void addVacancies(List<VacancyDTO> vacancies) {
        for (VacancyDTO vacancy : vacancies) {
            Company vacancyCompany = companyRepository.getReferenceById((long) vacancy.getCompanyID());
            Vacancy newVacancy = Vacancy.builder()
                    .company(vacancyCompany)
                    .title(vacancy.getTitle())
                    .minSalary(vacancy.getMinSalary())
                    .maxSalary(vacancy.getMaxSalary())
                    .link(vacancy.getLink())
                    .build();

            vacancyRepository.save(newVacancy);
        }
    }

    public List<Vacancy> getAllVacancies() {
        return vacancyRepository.findAll();
    }

    public void removeVacancy(Long id) {
        vacancyRepository.deleteById(id);
    }

    public Page<Vacancy> getVacanciesByPage(int pageId, int pageSize) {
        return vacancyRepository.findAll(PageRequest.of(pageId, pageSize));
    }
}
