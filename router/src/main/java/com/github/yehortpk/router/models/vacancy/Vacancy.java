package com.github.yehortpk.router.models.vacancy;

import com.github.yehortpk.router.models.company.Company;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Entity
@Table(name = "vacancy")
@Builder
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Vacancy {
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int vacancyId;
    @EqualsAndHashCode.Include
    private String link;
    @ToString.Include
    private String title;
    private int minSalary;
    private int maxSalary;
    private LocalDateTime parsedAt;
}
