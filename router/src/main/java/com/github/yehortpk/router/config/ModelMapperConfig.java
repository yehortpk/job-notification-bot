package com.github.yehortpk.router.config;

import com.github.yehortpk.router.models.company.Company;
import com.github.yehortpk.router.models.company.CompanyShortInfoDTO;
import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.models.filter.FilterShortInfoDTO;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import com.github.yehortpk.router.models.vacancy.VacancyShortDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Company to CompanyShortInfoDTO mapper
        TypeMap<Company, CompanyShortInfoDTO> companyToCompanyShortMap =
                modelMapper.createTypeMap(Company.class, CompanyShortInfoDTO.class);

        companyToCompanyShortMap.addMapping(Company::getTitle, CompanyShortInfoDTO::setCompanyTitle);

        // Filter to FilterShortInfo mapper
        TypeMap<Filter, FilterShortInfoDTO> filterToFilterShortInfoMap =
                modelMapper.createTypeMap(Filter.class, FilterShortInfoDTO.class);

        filterToFilterShortInfoMap.addMapping(Filter::getId, FilterShortInfoDTO::setFilterId);

        // Vacancy to VacancyDTO mapper
        TypeMap<Vacancy, VacancyDTO> vacancyToVacancyDTOMap =
                modelMapper.createTypeMap(Vacancy.class, VacancyDTO.class);

        vacancyToVacancyDTOMap.addMapping((vacancy -> vacancy.getCompany().getCompanyId()), VacancyDTO::setCompanyID);

        TypeMap<Vacancy, VacancyShortDTO> vacancyToVacancyShortDTOMap =
                modelMapper.createTypeMap(Vacancy.class, VacancyShortDTO.class);

        vacancyToVacancyShortDTOMap.addMapping((vacancy -> vacancy.getCompany().getCompanyId()), VacancyShortDTO::setCompanyId);
        vacancyToVacancyShortDTOMap.addMapping((Vacancy::getTitle), VacancyShortDTO::setVacancyTitle);
        vacancyToVacancyShortDTOMap.addMapping((Vacancy::getLink), VacancyShortDTO::setVacancyURL);

        return modelMapper;
    }
}
