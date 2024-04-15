package com.github.yehortpk.router.models.vacancy;

import lombok.*;

import java.io.Serializable;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class VacancyDTO implements Serializable {
    private int companyID;
    private String companyTitle;
    private int vacancyID;
    private String title;
    private int minSalary;
    private int maxSalary;
    @EqualsAndHashCode.Include
    private String link;
}

