package com.github.yehortpk.notifier.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "company")
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDAO {
    @Id
    private int companyId;
    private String jobsTemplateLink;
    private String singlePageRequestLink;
    private String beanClass;
    @ToString.Include
    private String title;
    @EqualsAndHashCode.Include
    private String link;
    private boolean isEnabled;
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<CompanyData> data;
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<CompanyHeader> headers;
}
