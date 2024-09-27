package com.github.yehortpk.router.mapper;

import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.models.vacancy.VacancyShortDTO;
import org.modelmapper.PropertyMap;

public class VacancyToVacancyShortDTOMapper extends PropertyMap<Vacancy, VacancyShortDTO> {
    @Override
    protected void configure() {
        map().setCompanyId(source.getCompany().getCompanyId());
        map().setVacancyTitle(source.getTitle());
        map().setVacancyURL(source.getLink());
    }
}
