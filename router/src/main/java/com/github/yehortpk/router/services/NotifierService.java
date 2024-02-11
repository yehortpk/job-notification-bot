package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.*;
import com.github.yehortpk.router.repositories.CompanyRepository;
import com.github.yehortpk.router.repositories.FilterRepository;
import com.github.yehortpk.router.utils.FilterParser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotifierService {
    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    FilterRepository filterRepository;

    @Autowired
    KafkaTemplate<String, VacancyNotificationDTO> kafkaTemplate;

    @Transactional
    public void notifyUsers(VacancyDTO vacancy) {
        CompanyDAO companyDAO = companyRepository.findByCompanyId(vacancy.getCompanyID());
        companyDAO.getSubscribers().forEach((subscriber) -> {
            boolean applicable = true;
            List<FilterDTO> filters = filterRepository.findByCompanyAndClient(companyDAO, subscriber)
                            .stream().map(FilterDTO::fromDAO).toList();

            if (!filters.isEmpty()) {
                for (FilterDTO filter : filters) {
                    if (isApplicable(vacancy.getTitle(), filter.getFilter())) {
                        applicable = true;
                        break;
                    } else {
                        applicable = false;
                    }
                }
            }

            System.out.println("Applicable: " + applicable);
            if (applicable) {

                VacancyNotificationDTO vacancyNotification = VacancyNotificationDTO.builder()
                        .chatId(subscriber.getChatId())
                        .vacancyTitle(vacancy.getTitle())
                        .companyTitle(companyDAO.getTitle())
                        .maxSalary(vacancy.getMaxSalary())
                        .minSalary(vacancy.getMinSalary())
                        .link(vacancy.getLink())
                        .build();

                kafkaTemplate.sendDefault(vacancyNotification);
            }
        });
    }

    public boolean isApplicable(String input, String filter) {
        FilterParser filterParser = new FilterParser(filter);

        List<String[]> binaryMatches = filterParser.getBinaryMatches();
        List<String> negativeMatches = filterParser.getNegativeMatches();
        List<String> defaultMatches = filterParser.getDefaultMatches();

        input = input.toLowerCase();

        for (String negativeMatch : negativeMatches) {
            if (input.contains(negativeMatch)) {
                return false;
            }
        }

        for (String defaultMatch : defaultMatches) {
            if (input.contains(defaultMatch)) {
                return true;
            }
        }

        for (String[] binaryMatch : binaryMatches) {
            for (String match : binaryMatch) {
                if (input.contains(match)) {
                    return true;
                }
            }
        }

        return false;
    }

}
