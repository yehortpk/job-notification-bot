package com.github.yehortpk.notifier.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CompanyDTO {
    private int companyId;
    private String jobsTemplateLink;
    private String singlePageRequestLink;
    private String beanClass;
    @ToString.Include
    private String title;
    @EqualsAndHashCode.Include
    private String link;
    private boolean isEnabled;
    private Map<String, String> data;
    private Map<String, String> headers;

    public static CompanyDTO fromDAO(CompanyDAO dao) {
        return CompanyDTO.builder()
                .companyId(dao.getCompanyId())
                .jobsTemplateLink(dao.getJobsTemplateLink())
                .singlePageRequestLink(dao.getSinglePageRequestLink())
                .beanClass(dao.getBeanClass())
                .title(dao.getTitle())
                .link(dao.getLink())
                .isEnabled(dao.isEnabled())
                .data(dao.getData().stream().collect(
                        Collectors.toMap(
                                CompanyData::getKey,
                                CompanyData::getValue
                        )))
                .headers(dao.getHeaders().stream().collect(
                        Collectors.toMap(
                                CompanyHeader::getKey,
                                CompanyHeader::getValue
                        )))
                .build();
    }
}
