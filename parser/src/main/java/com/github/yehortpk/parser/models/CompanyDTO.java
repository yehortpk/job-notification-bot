package com.github.yehortpk.parser.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

/**
 * DTO representing a company.
 */
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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
}
