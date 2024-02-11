package com.github.yehortpk.router.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CompanyShortInfoDTO {
    @JsonProperty("company_id")
    private long companyId;
    @JsonProperty("company_title")
    private String companyTitle;

    public static CompanyShortInfoDTO fromDAO(CompanyDAO dao) {
        return new CompanyShortInfoDTO(dao.getCompanyId(), dao.getTitle());
    }

    public static CompanyShortInfoDTO fromDTO(CompanyDTO dto) {
        return new CompanyShortInfoDTO(dto.getCompanyId(), dto.getTitle());
    }
}
