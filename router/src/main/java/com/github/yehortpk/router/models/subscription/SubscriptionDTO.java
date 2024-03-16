package com.github.yehortpk.router.models.subscription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDTO {
    private long chatId;
    private long companyId;
}
