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
    @JsonProperty("main_page_url")
    private String mainPageURL;
    /**
     * Template URL to the job pages. Use {page} bean for represent a page
     * placeholder. Further, in code it will be replaced to an actual page.
     */
    @JsonProperty("vacancies_url")
    private String vacanciesURL;
    /**
     * URL for the XHR. Used in single page site parsers.
     */
    @JsonProperty("api_vacancies_url")
    private String apiVacanciesURL;

    /**
     * Option for enable/disable company from parsing
     */
    @JsonProperty("parsing_enabled")
    private boolean isParsingEnabled;
    
    @JsonProperty("company_data")
    private Map<String, String> data;
    @JsonProperty("company_headers")
    private Map<String, String> headers;

    // todo change to modelmapper
    public static CompanyDTO fromDAO(Company dao) {
        return CompanyDTO.builder()
                .companyId(dao.getCompanyId())
                .vacanciesURL(dao.getVacanciesURL())
                .apiVacanciesURL(dao.getApiVacanciesURL())
                .beanClass(dao.getBeanClass())
                .title(dao.getTitle())
                .mainPageURL(dao.getMainPageURL())
                .isParsingEnabled(dao.isParsingEnabled())
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
