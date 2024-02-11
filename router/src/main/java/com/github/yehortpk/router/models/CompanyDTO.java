package com.github.yehortpk.router.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CompanyDTO {
    private int companyId;
    private String jobsTemplateLink;
    private String beanClass;
    @ToString.Include
    private String title;
    @EqualsAndHashCode.Include
    private String link;
    private boolean isEnabled;
    private Set<ClientDTO> subscribers;

    public static CompanyDTO fromDAO(CompanyDAO dao) {
        return CompanyDTO.builder()
                .companyId(dao.getCompanyId())
                .jobsTemplateLink(dao.getJobsTemplateLink())
                .beanClass(dao.getBeanClass())
                .title(dao.getTitle())
                .link(dao.getLink())
                .isEnabled(dao.isEnabled())
                .subscribers(dao.getSubscribers().stream().map(ClientDTO::fromDAOWithoutSubscriptions)
                        .collect(Collectors
                                .toSet()))
                .build();
    }

    public static CompanyDTO fromDAOWithoutClients(CompanyDAO dao) {
        return CompanyDTO.builder()
                .companyId(dao.getCompanyId())
                .jobsTemplateLink(dao.getJobsTemplateLink())
                .beanClass(dao.getBeanClass())
                .title(dao.getTitle())
                .link(dao.getLink())
                .isEnabled(dao.isEnabled())
                .subscribers(new HashSet<>())
                .build();
    }
}
