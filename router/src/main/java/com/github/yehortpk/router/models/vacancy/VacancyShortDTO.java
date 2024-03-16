package com.github.yehortpk.router.models.vacancy;

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
