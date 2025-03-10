package com.github.yehortpk.router.mapper;

import com.github.yehortpk.router.models.company.Company;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import org.modelmapper.PropertyMap;

public class VacancyDTOToVacancyMapper extends PropertyMap<VacancyDTO, Vacancy> {
    @Override
    protected void configure() {
        using(ctx -> {
            int companyID = ((VacancyDTO) ctx.getSource()).getCompanyID();
            return Company.builder().companyId(companyID).build();
        }).map(source).setCompany(null);
    }
}
