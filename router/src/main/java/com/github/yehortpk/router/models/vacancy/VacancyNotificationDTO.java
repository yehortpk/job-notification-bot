package com.github.yehortpk.router.models.vacancy;

import lombok.*;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacancyNotificationDTO {
    private long chatId;
    private long filterId;
    private String filter;
    private String companyTitle;
    private String vacancyTitle;
    private int minSalary;
    private int maxSalary;
    @EqualsAndHashCode.Include
    private String link;
}
