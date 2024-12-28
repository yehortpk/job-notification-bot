package com.github.yehortpk.router.mapper;

import com.github.yehortpk.router.models.vacancy.VacanciesPageDTO;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.PropertyMap;
import org.springframework.data.domain.Page;

@Slf4j
public class DataPageToVacanciesPageDTOMapper extends PropertyMap<Page<Vacancy>, VacanciesPageDTO> {
    @Override
    protected void configure() {
        map(source.getContent(), destination.getVacancies());
        map().setTotalVacancies((int) source.getTotalElements());
        map().setPageSize(source.getSize());
        map().setCurrentPage(source.getNumber());
    }
}
