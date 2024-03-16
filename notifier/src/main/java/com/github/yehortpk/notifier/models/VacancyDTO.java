package com.github.yehortpk.notifier.models;

import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VacancyDTO implements Serializable {
    private int companyID;
    private int vacancyID;
    private String title;
    private int minSalary;
    private int maxSalary;
    @EqualsAndHashCode.Include
    private String link;
}
