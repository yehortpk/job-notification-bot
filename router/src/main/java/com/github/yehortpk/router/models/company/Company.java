package com.github.yehortpk.router.models.company;

import com.github.yehortpk.router.models.vacancy.Vacancy;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "company")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    @Id
    @EqualsAndHashCode.Include
    private int companyId;
    private String beanClass;
    @ToString.Include
    private String title;

    @Column(name = "main_page_url")
    private String mainPageURL;
    @Column(name = "vacancies_url")
    private String vacanciesURL;
    @Column(name = "api_vacancies_url")
    private String apiVacanciesURL;

    @Column(name = "parsing_enabled")
    private boolean isParsingEnabled;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    private Set<Vacancy> vacancies;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    private Set<CompanyData> companyData;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    private Set<CompanyHeader> companyHeaders;
}
