package com.github.yehortpk.subscriberbot.dtos;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SubscriptionDTO {
    private long companyId;
    private long chatId;
}
