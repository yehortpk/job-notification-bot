package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.client.Client;
import com.github.yehortpk.router.models.company.Company;
import com.github.yehortpk.router.models.company.CompanyDTO;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    public List<CompanyDTO> getCompanies() {
        return companyRepository.findByIsEnabledTrue().stream().map(CompanyDTO::fromDAO).toList();
    }

    public Set<Vacancy> getVacancies(int companyId) {
        Company company = companyRepository.findByCompanyId(companyId);
        return company.getVacancies();
    }

    public Set<Client> getSubscribers(int companyId) {
        Company company = companyRepository.findByCompanyId(companyId);
        return company.getSubscribers();
    }

    public Company findCompanyById(long companyId) {
        return companyRepository.findByCompanyId(companyId);
    }

    public void saveCompany(Company company) {
        companyRepository.save(company);
    }
}
