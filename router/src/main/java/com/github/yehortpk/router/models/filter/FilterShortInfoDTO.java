package com.github.yehortpk.router.models.filter;

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
}
