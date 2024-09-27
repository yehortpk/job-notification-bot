package com.github.yehortpk.router.mapper;

import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.models.filter.FilterShortInfoDTO;
import org.modelmapper.PropertyMap;

public class FilterToFilterShortInfoDTOMapper extends PropertyMap<Filter, FilterShortInfoDTO> {
    @Override
    protected void configure() {
        map().setFilterId(source.getId());
    }

}
