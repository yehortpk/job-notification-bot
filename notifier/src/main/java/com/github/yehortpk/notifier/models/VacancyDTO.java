package com.github.yehortpk.notifier.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@Builder
@ToString
public class VacancyDTO {
    private int companyID;
    private int vacancyID;
    private String title;
    private int minSalary;
    private int maxSalary;
    @EqualsAndHashCode.Include
    private String link;

    public static VacancyDTO fromDAO(VacancyDAO dao) {
        return VacancyDTO.builder()
                .companyID(dao.getCompanyID())
                .vacancyID(dao.getVacancyId())
                .title(dao.getTitle())
                .minSalary(dao.getMinSalary())
                .maxSalary(dao.getMaxSalary())
                .link(dao.getLink())
                .build();
    }

    public VacancyDAO toDAO() {
        return VacancyDAO.builder()
                .companyID(this.getCompanyID())
                .vacancyId(this.getVacancyID())
                .title(this.getTitle())
                .minSalary(this.getMinSalary())
                .maxSalary(this.getMaxSalary())
                .link(this.getLink())
                .build();
    }
}
