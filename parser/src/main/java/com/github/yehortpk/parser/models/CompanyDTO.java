package com.github.yehortpk.parser.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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
}
