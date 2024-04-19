package com.github.yehortpk.parser.models;

import lombok.*;

import java.io.Serializable;

/**
 * DTO representing a vacancy
 */
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VacancyDTO implements Serializable {
    private int companyID;
    private String companyTitle;
    private int vacancyID;
    /**
     * Vacancy title
     */
    private String title;
    private int minSalary;
    private int maxSalary;
    /**
     * Full link to the vacancy
     */
    @EqualsAndHashCode.Include
    private String link;
}
