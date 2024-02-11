package com.github.yehortpk.router.models;

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
    private int vacancyID;
    private String title;
    private int minSalary;
    private int maxSalary;
    @EqualsAndHashCode.Include
    private String link;
}

