package com.github.yehortpk.router.models;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@Builder
public class VacancyNotificationDTO {
    private long chatId;
    private String companyTitle;
    private String vacancyTitle;
    private int minSalary;
    private int maxSalary;
    @EqualsAndHashCode.Include
    private String link;
}
