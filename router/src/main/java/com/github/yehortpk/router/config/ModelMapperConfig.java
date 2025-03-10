package com.github.yehortpk.router.config;

import com.github.yehortpk.router.mapper.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(false)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        modelMapper.addMappings(new CompanyToCompanyShortDTOMapper());
        modelMapper.addMappings(new FilterToFilterDTOMapper());
        modelMapper.addMappings(new VacancyToVacancyDTOMapper());
        modelMapper.addMappings(new VacancyDTOToVacancyMapper());
        modelMapper.addMappings(new VacancyToVacancyShortDTOMapper());
        modelMapper.addMappings(new DataPageToVacanciesPageDTOMapper());
        modelMapper.addMappings(new VacancyToVacancyCompanyDTOMapper());
        modelMapper.addMappings(new ParsingProgressDTOToEntityMapper());

        return modelMapper;
    }
}
