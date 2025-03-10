package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.models.VacancyDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * This class sends vacancies notification for the router service
 */
@Service
@RequiredArgsConstructor
public class NotifierService {
    private final KafkaTemplate<String, VacancyDTO> routerService;

    /**
     * Notify router service about new vacancies
     * @param newVacancies set of new vacancies
     */
    public void notifyNewVacancies(Set<VacancyDTO> newVacancies) {
        synchronized (this) {
            for (VacancyDTO newVacancy : newVacancies) {
                routerService.sendDefault(newVacancy);
            }
        }
    }
}
