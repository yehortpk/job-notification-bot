package com.github.yehortpk.router.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class FilterShortDTO {
    private long companyId;
    private long clientId;
    private String filter;
}
