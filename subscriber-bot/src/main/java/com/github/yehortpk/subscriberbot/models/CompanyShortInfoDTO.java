package com.github.yehortpk.subscriberbot.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CompanyShortInfoDTO {
    @JsonProperty("company_id")
    private long companyId;
    @JsonProperty("title")
    private String companyTitle;
}
