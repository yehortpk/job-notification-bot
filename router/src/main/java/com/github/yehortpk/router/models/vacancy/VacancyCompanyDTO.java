package com.github.yehortpk.router.models.vacancy;

import com.github.yehortpk.router.models.company.CompanyShortInfoDTO;
import lombok.Data;

@Data
public class VacancyCompanyDTO {
    private String title;
    private int minSalary;
    private int maxSalary;
    private String URL;
    private CompanyShortInfoDTO company;
}
