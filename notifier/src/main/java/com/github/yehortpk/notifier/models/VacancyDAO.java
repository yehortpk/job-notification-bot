package com.github.yehortpk.notifier.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@Entity
@Table(name = "vacancy")
@Builder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VacancyDAO {
    private int companyID;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int vacancyId;
    @EqualsAndHashCode.Include
    private String link;
    private String title;
    private int minSalary;
    private int maxSalary;
}
