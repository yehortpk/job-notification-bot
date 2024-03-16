package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.company.Company;
import com.github.yehortpk.router.models.company.CompanyDTO;
import com.github.yehortpk.router.models.company.CompanyData;
import com.github.yehortpk.router.models.company.CompanyShortInfoDTO;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import com.github.yehortpk.router.repositories.CompanyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CompanyService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CompanyRepository companyRepository;

    public List<CompanyDTO> getCompanies() {
        return companyRepository.findByIsEnabledTrue().stream().map(CompanyDTO::fromDAO).toList();
    }

    public List<VacancyDTO> getVacancies(int companyId) {
        Company company = companyRepository.findByCompanyId(companyId);
        Set<Vacancy> vacancies = company.getVacancies();
        return vacancies.stream().map(vacancy -> modelMapper.map(vacancy, VacancyDTO.class)).toList();
    }
}
