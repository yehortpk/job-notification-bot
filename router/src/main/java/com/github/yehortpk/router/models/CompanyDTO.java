package com.github.yehortpk.router.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@ToString
@EqualsAndHashCode
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
                .subscribers(dao.getSubscribers().stream().map(ClientDTO::fromDAO).collect(Collectors.toSet()))
                .build();
    }

    public CompanyDAO toDAO() {
        return CompanyDAO.builder()
                .companyId(this.getCompanyId())
                .jobsTemplateLink(this.getJobsTemplateLink())
                .beanClass(this.getBeanClass())
                .title(this.getTitle())
                .link(this.getLink())
                .isEnabled(this.isEnabled())
                .subscribers(this.getSubscribers().stream().map(ClientDTO::toDAO).collect(Collectors.toSet()))
                .build();
    }
}
