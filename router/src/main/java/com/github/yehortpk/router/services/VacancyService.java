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

import java.util.ArrayList;
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
        List<Long> existentVacanciesID = vacancyRepository.findAllIds();
        List<Vacancy> newVacancies = new ArrayList<>();
        for (VacancyDTO vacancy : vacancies) {
            if (existentVacanciesID.contains((long) vacancy.getVacancyID())) {
                continue;
            }

            Company vacancyCompany = companyRepository.getReferenceById((long) vacancy.getCompanyID());
            Vacancy newVacancy = Vacancy.builder()
                    .company(vacancyCompany)
                    .title(vacancy.getTitle())
                    .minSalary(vacancy.getMinSalary())
                    .maxSalary(vacancy.getMaxSalary())
                    .link(vacancy.getLink())
                    .parsedAt(vacancy.getParsedAt())
                    .build();

            newVacancies.add(newVacancy);
        }
        vacancyRepository.saveAll(newVacancies);
    }

    public void addVacancy(VacancyDTO vacancy) {
        Company vacancyCompany = companyRepository.getReferenceById((long) vacancy.getCompanyID());
        Vacancy newVacancy = Vacancy.builder()
                .company(vacancyCompany)
                .title(vacancy.getTitle())
                .minSalary(vacancy.getMinSalary())
                .maxSalary(vacancy.getMaxSalary())
                .link(vacancy.getLink())
                .parsedAt(vacancy.getParsedAt())
                .build();

        vacancyRepository.save(newVacancy);
    }

    public List<Vacancy> getAllVacancies() {
        return vacancyRepository.findAll();
    }

    public void removeVacancy(Long id) {
        vacancyRepository.deleteById(id);
    }

    public Page<Vacancy> getVacancies(Pageable pageable) {
        return vacancyRepository.findAll(pageable);
    }

    public Page<Vacancy> getVacancies(String query, PageRequest pageable) {
        return vacancyRepository.findByTitleContaining(query, pageable);
    }

    public void removeVacanciesByUrlsIn(List<String> urls) {
        vacancyRepository.deleteByLinkIn(urls);
    }


}
