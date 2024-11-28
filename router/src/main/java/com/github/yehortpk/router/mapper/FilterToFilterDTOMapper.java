package com.github.yehortpk.router.mapper;

import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.models.filter.FilterDTO;
import org.modelmapper.PropertyMap;

public class FilterToFilterDTOMapper extends PropertyMap<Filter, FilterDTO> {
    @Override
    protected void configure() {
        map().setFilterId(source.getId());
        map().setClientId(source.getClient().getChatId());
    }

}
