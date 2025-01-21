package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.models.ParsingProgressDTO;
import com.github.yehortpk.parser.models.VacancyDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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


    @Value("${router-vacancies-url}")
    private String routerVacanciesURL;

    /**
     * Notify router service about new vacancies
     * @param newVacancies set of new vacancies
     */
    public void notifyNewVacancies(Set<VacancyDTO> newVacancies) {
        for (VacancyDTO newVacancy : newVacancies) {
            routerService.sendDefault(newVacancy);
        }
    }

    public void notifyOutdatedVacancies(Set<String> outdatedVacanciesIds) {
        HttpEntity<Set<String>> request =
                new HttpEntity<>(outdatedVacanciesIds);
        restTemplate.exchange(routerVacanciesURL, HttpMethod.DELETE, request, Void.class);
    }

    @Value("${router-parser-url}")
    private String routerParserURL;

    public void notifyFinishedProgress(ParsingProgressDTO parsingProgress) {
        HttpEntity<ParsingProgressDTO> request =
                new HttpEntity<>(parsingProgress);
        restTemplate.exchange(routerParserURL + "/progress", HttpMethod.POST, request, Void.class);
    }
}
