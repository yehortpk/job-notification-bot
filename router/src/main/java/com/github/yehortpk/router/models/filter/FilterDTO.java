package com.github.yehortpk.router.models.filter;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class FilterDTO {
    private long companyId;
    private long clientId;
    private String filter;
}
