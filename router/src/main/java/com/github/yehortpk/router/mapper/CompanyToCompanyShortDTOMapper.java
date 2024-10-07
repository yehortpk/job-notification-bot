package com.github.yehortpk.router.mapper;

import com.github.yehortpk.router.models.company.Company;
import com.github.yehortpk.router.models.company.CompanyShortInfoDTO;
import org.modelmapper.PropertyMap;

public class CompanyToCompanyShortDTOMapper extends PropertyMap<Company, CompanyShortInfoDTO> {
    @Override
    protected void configure() {
        map().setCompanyTitle(source.getTitle());
        map().setCompanyURL(source.getLink());
    }
}
