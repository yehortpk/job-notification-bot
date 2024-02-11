package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.CompanyShortInfoDTO;
import com.github.yehortpk.router.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {
    @Autowired
    CompanyRepository companyRepository;

    public List<CompanyShortInfoDTO> getCompanies() {
        return companyRepository.findAll().stream().map(CompanyShortInfoDTO::fromDAO).toList();
    }
}
