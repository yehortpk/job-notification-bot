package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.company.CompanyDTO;
import com.github.yehortpk.router.models.company.CompanyShortInfoDTO;
import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import com.github.yehortpk.router.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    CompanyService companyService;

    @GetMapping
    public List<CompanyDTO> getCompanies() {
        return companyService.getCompanies();
    }

    @GetMapping("/{company_id}/vacancies")
    public List<VacancyDTO> getVacancies(@PathVariable("company_id") int companyId) {
        return companyService.getVacancies(companyId);
    }
}
