package com.github.yehortpk.notifier.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@Table(name = "company")
@ToString
@EqualsAndHashCode
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDAO {
    @Id
    private int companyId;
    private String jobsTemplateLink;
    private String beanClass;
    @ToString.Include
    private String title;
    @EqualsAndHashCode.Include
    private String link;
    private boolean isEnabled;
}
