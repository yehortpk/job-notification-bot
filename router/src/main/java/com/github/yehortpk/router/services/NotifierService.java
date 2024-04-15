package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import com.github.yehortpk.router.models.vacancy.VacancyNotificationDTO;
import com.github.yehortpk.router.utils.FilterParser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotifierService {
    private final CompanyService companyService;
    private final FilterService filterService;
    private final KafkaTemplate<String, VacancyNotificationDTO> kafkaTemplate;

    @Transactional
    public void notifyUsers(VacancyDTO vacancy) {
        companyService.getSubscribers(vacancy.getCompanyID()).forEach((subscriber) -> {
            boolean isApplicable = true;
            List<Filter> filters = filterService.findByCompanyIdAndClientId(
                    vacancy.getCompanyID(), subscriber.getChatId());

            if (!filters.isEmpty()) {
                for (Filter filter : filters) {
                    if (isVacancyApplicable(vacancy.getTitle(), filter.getFilter())) {
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

    public boolean isVacancyApplicable(String vacancyTitle, String filter) {
        FilterParser filterParser = new FilterParser(filter);

        List<String[]> binaryMatches = filterParser.getBinaryMatches();
        List<String> negativeMatches = filterParser.getNegativeMatches();
        List<String> mandatoryMatches = filterParser.getMandatoryMatches();

        vacancyTitle = vacancyTitle.toLowerCase();

        for (String negativeMatch : negativeMatches) {
            if (vacancyTitle.contains(negativeMatch)) {
                return false;
            }
        }

        for (String defaultMatch : mandatoryMatches) {
            if (!vacancyTitle.contains(defaultMatch)) {
                return false;
            }
        }

        boolean result = true;
        for (String[] binaryMatch : binaryMatches) {
            for (String match : binaryMatch) {
                if (vacancyTitle.contains(match)) {
                    return true;
                }
            }
            result = false;
        }

        return result;
    }

}
