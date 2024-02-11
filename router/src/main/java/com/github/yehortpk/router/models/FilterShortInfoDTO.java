package com.github.yehortpk.router.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class FilterShortInfoDTO {
    private long filterId;
    private String filter;

    public static FilterShortInfoDTO fromDAO(FilterDAO dao) {
        return new FilterShortInfoDTO(dao.getId(), dao.getFilter());
    }
}
