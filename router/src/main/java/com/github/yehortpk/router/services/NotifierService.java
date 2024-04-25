package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import com.github.yehortpk.router.models.vacancy.VacancyNotificationDTO;
import com.github.yehortpk.router.utils.FilterParser;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service provides notification methods
 */
@Service
@RequiredArgsConstructor
public class NotifierService {
    private final CompanyService companyService;
    private final FilterService filterService;
    private final KafkaTemplate<String, VacancyNotificationDTO> kafkaTemplate;

    /**
     * Notifies users about new vacancy
     * @param vacancy vacancy for notification
     */
    public void notifyUsers(VacancyDTO vacancy) {
        companyService.getSubscribers(vacancy.getCompanyID()).forEach((subscriber) -> {
            boolean isApplicable = true;
            List<Filter> filters = filterService.findByCompanyIdAndClientId(
                    vacancy.getCompanyID(), subscriber.getChatId());

            if (!filters.isEmpty()) {
                for (Filter filter : filters) {
                    FilterParser filterParser = new FilterParser(filter.getFilter());
                    if (filterParser.isVacancyApplicable(vacancy.getTitle())) {
                        isApplicable = true;
                        break;
                    } else {
                        isApplicable = false;
                    }
                }
            }

            if (isApplicable) {

                VacancyNotificationDTO vacancyNotification = VacancyNotificationDTO.builder()
                        .chatId(subscriber.getChatId())
                        .vacancyTitle(vacancy.getTitle())
                        .companyTitle(vacancy.getCompanyTitle())
                        .maxSalary(vacancy.getMaxSalary())
                        .minSalary(vacancy.getMinSalary())
                        .link(vacancy.getLink())
                        .build();

                kafkaTemplate.sendDefault(vacancyNotification);
            }
        });
    }
}
