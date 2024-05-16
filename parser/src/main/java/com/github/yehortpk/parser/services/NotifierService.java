package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.models.VacancyDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

/**
 * This class sends vacancies notification for the router service
 */
@Service
@RequiredArgsConstructor
public class NotifierService {
    private final KafkaTemplate<String, VacancyDTO> routerService;
    private final RestTemplate restTemplate;


    /**
     * Notify router service about new vacancies
     * @param newVacancies set of new vacancies
     */
    public void notifyNewVacancies(Set<VacancyDTO> newVacancies) {
        for (VacancyDTO newVacancy : newVacancies) {
            routerService.sendDefault(newVacancy);
        }
    }

    @Value("${router-vacancies-url}")
    private String routerVacanciesURL;

    public void notifyOutdatedVacancies(Set<VacancyDTO> outdatedVacancies) {
        outdatedVacancies.forEach(vacancy -> restTemplate.delete(routerVacanciesURL + "/" + vacancy.getVacancyID()));
    }
}
