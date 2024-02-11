package com.github.yehortpk.subscriberbot.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class FilterShortDTO {
    private int companyId;
    private long clientId;
    private String filter;
}