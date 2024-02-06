package com.github.yehortpk.notifier.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Entity
@Table(name = "vacancy")
@Builder
@NoArgsConstructor
public class VacancyDAO {
    private int companyID;
    private int vacancyId;
    @Id
    private String link;
    private String title;
    private int minSalary;
    private int maxSalary;
}
