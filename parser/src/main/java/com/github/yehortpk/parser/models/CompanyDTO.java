package com.github.yehortpk.parser.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.yehortpk.parser.domain.parsers.ComponentSiteParser;
import com.github.yehortpk.parser.domain.parsers.StaticSiteParser;
import com.github.yehortpk.parser.domain.parsers.SiteParser;
import com.github.yehortpk.parser.domain.parsers.APISiteParser;
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
     * Template URL to the job pages. Template uses in {@link StaticSiteParser} and {@link ComponentSiteParser}.
     * Use {page} bean for represent a page placeholder. Further, in code it will be replaced to an actual page
     */
    @JsonProperty("jobs_template_link")
    private String jobsTemplateLink;
    /**
     * URL for the XHR. Used in {@link APISiteParser}
     */
    @JsonProperty("single_page_request_link")
    private String singlePageRequestLink;
    /**
     * Bean name that accords to the {@link SiteParser} implementation. Used in spring bean context
     * to retrieve {@link SiteParser} bean for bean name
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
}
