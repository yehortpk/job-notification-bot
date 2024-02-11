package com.github.yehortpk.subscriberbot.dtos;

import lombok.*;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VacancyNotificationDTO {
    private long chatId;
    private String companyTitle;
    private String vacancyTitle;
    private int minSalary;
    private int maxSalary;
    @EqualsAndHashCode.Include
    private String link;
}
