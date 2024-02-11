package com.github.yehortpk.subscriberbot.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class FilterShortInfoDTO {
    private int filterId;
    private String filter;
}
