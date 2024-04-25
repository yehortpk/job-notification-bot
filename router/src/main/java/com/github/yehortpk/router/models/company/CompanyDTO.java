package com.github.yehortpk.router.models.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * DTO representing a company.
 */
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
    /**
     * Template URL to the job pages. Used in multi/component page site parsers. Use {page} bean for represent a page
     * placeholder. Further, in code it will be replaced to an actual page.
     */
    @JsonProperty("jobs_template_link")
    private String jobsTemplateLink;
    /**
     * URL for the XHR. Used in single page site parsers.
     */
    @JsonProperty("single_page_request_link")
    private String singlePageRequestLink;
    /**
     * Bean name that accords to the site parser @Bean name. Used in spring bean context to retrieve with the bean name
     */
    @JsonProperty("bean_class")
    private String beanClass;
    @ToString.Include
    @JsonProperty("title")
    private String title;
    /**
     * Main URL to the company domain
     */
    @EqualsAndHashCode.Include
    @JsonProperty("link")
    private String link;
    /**
     * Option for enable/disable company from parsing
     */
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
