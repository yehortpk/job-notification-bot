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

import java.util.HashSet;
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

    public void notifyUsers(VacancyDTO vacancy) {
        List<FilterDTO> filters = filterService.getAllFilters().stream()
                .map(filter -> modelMapper.map(filter, FilterDTO.class))
                .toList();

        // Prevent duplicated notifications in case the vacancy would match many filters
        HashSet<VacancyDTO> notifiedVacancies = new HashSet<>();

        filters.forEach(filter -> {
            FilterParser filterParser = new FilterParser(filter.getFilter());
            if (!notifiedVacancies.contains(vacancy) && filterParser.isStringApplicable(vacancy.getTitle())) {
                VacancyNotificationDTO vacancyNotification = VacancyNotificationDTO.builder()
                        .vacancyTitle(vacancy.getTitle())
                        .filter(filter.getFilter())
                        .filterId(filter.getFilterId())
                        .minSalary(vacancy.getMinSalary())
                        .maxSalary(vacancy.getMaxSalary())
                        .companyTitle(vacancy.getCompanyTitle())
                        .chatId(filter.getClientId())
                        .link(vacancy.getLink())
                        .isRemote(vacancy.isRemote())
                        .build();

                notifyUser(vacancyNotification);
                notifiedVacancies.add(vacancy);
            }
        });
    }

    public void notifyUser(VacancyNotificationDTO vacancy) {
        kafkaTemplate.sendDefault(vacancy);
    }
}
