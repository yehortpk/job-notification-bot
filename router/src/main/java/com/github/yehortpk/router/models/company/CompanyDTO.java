package com.github.yehortpk.router.models.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    @JsonProperty("company_id")
    private int companyId;
    @JsonProperty("jobs_template_link")
    private String jobsTemplateLink;
    @JsonProperty("single_page_request_link")
    private String singlePageRequestLink;
    @JsonProperty("bean_class")
    private String beanClass;
    @ToString.Include
    @JsonProperty("title")
    private String title;
    @EqualsAndHashCode.Include
    @JsonProperty("link")
    private String link;
    @JsonProperty("is_enabled")
    private boolean isEnabled;
    @JsonProperty("company_data")
    private Map<String, String> data;
    @JsonProperty("company_headers")
    private Map<String, String> headers;

    // todo change to modelmapper
    public static CompanyDTO fromDAO(Company dao) {
        return CompanyDTO.builder()
                .companyId(dao.getCompanyId())
                .jobsTemplateLink(dao.getJobsTemplateLink())
                .singlePageRequestLink(dao.getSinglePageRequestLink())
                .beanClass(dao.getBeanClass())
                .title(dao.getTitle())
                .link(dao.getLink())
                .isEnabled(dao.isEnabled())
                .data(dao.getCompanyData().stream().collect(Collectors.toMap(
                        CompanyData::getKey,
                        CompanyData::getValue
                )))
                .headers(dao.getCompanyHeaders().stream().collect(Collectors.toMap(
                        CompanyHeader::getKey,
                        CompanyHeader::getValue
                )))
                .build();
    }
}
