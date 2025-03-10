package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.models.VacancyDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final RestTemplate restTemplate;

    @Value("${router-vacancies-url}")
    private String routerVacanciesURL;

    public void updateVacancy(VacancyDTO vacancy) {
        HttpEntity<VacancyDTO> request =
                new HttpEntity<>(vacancy);
        restTemplate.put(routerVacanciesURL, request);
    }

    public void deleteVacancy(int vacancyID) {
        restTemplate.delete(routerVacanciesURL + "/" + vacancyID);
    }
}
