package com.github.yehortpk.notifier.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

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

    public static CompanyDTO fromDAO(CompanyDAO dao) {
        return CompanyDTO.builder()
                .companyId(dao.getCompanyId())
                .jobsTemplateLink(dao.getJobsTemplateLink())
                .beanClass(dao.getBeanClass())
                .title(dao.getTitle())
                .link(dao.getLink())
                .isEnabled(dao.isEnabled())
                .build();
    }
}
