package com.github.yehortpk.router.models.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompanyShortInfoDTO {
    @JsonProperty("company_id")
    private long companyId;
    @JsonProperty("company_title")
    private String companyTitle;
}
