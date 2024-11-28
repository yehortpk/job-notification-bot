package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.company.CompanyDTO;
import com.github.yehortpk.router.models.vacancy.VacancyShortDTO;
import com.github.yehortpk.router.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller responsible for handling company related requests
 */
@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class    CompanyController {
    private final CompanyService companyService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<CompanyDTO> getCompanies() {
        return companyService.getCompanies().stream().map(CompanyDTO::fromDAO).toList();
    }

    @GetMapping("/{company_id}/vacancies")
    public List<VacancyShortDTO> getVacancies(@PathVariable("company_id") int companyId) {
        return companyService.getVacancies(companyId).stream().map(vacancy ->
                modelMapper.map(vacancy, VacancyShortDTO.class)).toList();
    }
}
