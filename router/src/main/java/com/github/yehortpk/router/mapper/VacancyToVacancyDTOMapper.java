package com.github.yehortpk.router.mapper;

import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import org.modelmapper.PropertyMap;

public class VacancyToVacancyDTOMapper extends PropertyMap<Vacancy, VacancyDTO> {
    @Override
    protected void configure() {
        map().setCompanyID(source.getCompany().getCompanyId());
    }
}
