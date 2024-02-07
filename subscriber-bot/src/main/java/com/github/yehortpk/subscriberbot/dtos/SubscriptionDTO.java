package com.github.yehortpk.subscriberbot.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SubscriptionDTO {
    @JsonProperty("company_id")
    private long companyId;
    @JsonProperty("chat_id")
    private long chatId;
}
