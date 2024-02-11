package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.CompanyShortInfoDTO;
import com.github.yehortpk.router.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    CompanyService companyService;

    @GetMapping
    public List<CompanyShortInfoDTO> getCompanies() {
        return companyService.getCompanies();
    }
}
