package com.github.yehortpk.router.mapper;

import com.github.yehortpk.router.models.company.CompanyShortInfoDTO;
import com.github.yehortpk.router.models.vacancy.VacanciesPageDTO;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.models.vacancy.VacancyCompanyDTO;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.data.domain.Page;

import java.util.List;

@Slf4j
public class DataPageToVacanciesPageDTOMapper extends PropertyMap<Page<Vacancy>, VacanciesPageDTO> {
    @Override
    protected void configure() {
        using(new VacanciesListToVacancyCompanyDTO()).map(source.getContent(), destination.getVacancies());
        map().setTotalVacancies((int) source.getTotalElements());
        map().setPageSize(source.getSize());
        map().setCurrentPage(source.getNumber());
    }
}

class VacanciesListToVacancyCompanyDTO implements Converter<List<Vacancy>, List<VacancyCompanyDTO>> {
    @Override
    public List<VacancyCompanyDTO> convert(MappingContext<List<Vacancy>, List<VacancyCompanyDTO>> context) {
        List<Vacancy> source = context.getSource();

        return source.stream().map(vacancy -> {
            VacancyCompanyDTO vacancyCompanyDTO = new VacancyCompanyDTO();
            vacancyCompanyDTO.setMinSalary(vacancy.getMinSalary());
            vacancyCompanyDTO.setMaxSalary(vacancy.getMaxSalary());
            vacancyCompanyDTO.setTitle(vacancy.getTitle());
            vacancyCompanyDTO.setCompany(new CompanyShortInfoDTO(vacancy.getCompany().getCompanyId(),
                    vacancy.getCompany().getTitle()));

            return  vacancyCompanyDTO;

        }).toList();
    }
}
