package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.filter.FilterDTO;
import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import com.github.yehortpk.router.models.vacancy.VacancyNotificationDTO;
import com.github.yehortpk.router.utils.FilterParser;
import lombok.Getter;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotifierService {
    private final FilterService filterService;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, VacancyNotificationDTO> kafkaTemplate;

    @Getter
    @Value("${notifier.enabled}")
    private boolean notifierEnabled;

    public void notifyUsers(List<VacancyDTO> vacancies) {
        List<FilterDTO> filters = filterService.getAllFilters().stream()
                .map(filter -> modelMapper.map(filter, FilterDTO.class))
                .toList();

        filters.forEach(filter -> {
            FilterParser filterParser = new FilterParser(filter.getFilter());
            vacancies.forEach(vacancyDTO -> {
                if (filterParser.isStringApplicable(vacancyDTO.getTitle())) {
                    VacancyNotificationDTO vacancy = VacancyNotificationDTO.builder()
                            .vacancyTitle(vacancyDTO.getTitle())
                            .filter(filter.getFilter())
                            .filterId(filter.getFilterId())
                            .minSalary(vacancyDTO.getMinSalary())
                            .maxSalary(vacancyDTO.getMaxSalary())
                            .companyTitle(vacancyDTO.getCompanyTitle())
                            .chatId(filter.getClientId())
                            .link(vacancyDTO.getLink())
                            .build();

                    notifyUser(vacancy);
                }
            });
        });
    }

    public void notifyUser(VacancyNotificationDTO vacancy) {
        kafkaTemplate.sendDefault(vacancy);
    }
}
