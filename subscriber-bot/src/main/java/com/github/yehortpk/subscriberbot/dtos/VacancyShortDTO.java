package com.github.yehortpk.subscriberbot.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VacancyShortDTO {
    private int companyId;
    private String vacancyTitle;
    private String vacancyURL;
}
