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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    @Id
    private int companyId;
    private String singlePageRequestLink;
    private String jobsTemplateLink;
    private String beanClass;
    @ToString.Include
    private String title;
    @EqualsAndHashCode.Include
    private String link;
    private boolean isEnabled;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    private Set<Vacancy> vacancies;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    private Set<CompanyData> companyData;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    private Set<CompanyHeader> companyHeaders;
}
