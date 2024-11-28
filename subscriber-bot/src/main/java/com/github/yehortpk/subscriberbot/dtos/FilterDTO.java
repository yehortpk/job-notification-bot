package com.github.yehortpk.subscriberbot.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class FilterDTO {
    private long filterId;
    private long clientId;
    private String filter;
}