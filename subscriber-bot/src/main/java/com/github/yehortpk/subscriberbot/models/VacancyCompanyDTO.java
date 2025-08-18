package com.github.yehortpk.subscriberbot.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VacancyCompanyDTO {
    private String title;
    private int minSalary;
    private int maxSalary;
    private String URL;
    private CompanyShortInfoDTO company;
    private LocalDateTime parsedAt;
}
