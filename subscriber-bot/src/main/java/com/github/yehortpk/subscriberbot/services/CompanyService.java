package com.github.yehortpk.subscriberbot.services;

import com.github.yehortpk.subscriberbot.models.CompanyShortInfoDTO;
import com.github.yehortpk.subscriberbot.models.VacancyShortDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${company-url}")
    private String companyURL;

    public List<CompanyShortInfoDTO> getCompaniesList() {
        ParameterizedTypeReference<List<CompanyShortInfoDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(companyURL,
                HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }

    public List<VacancyShortDTO> getCompanyVacancies(long companyId) {
        ParameterizedTypeReference<List<VacancyShortDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(companyURL + String.format("/%s/vacancies", companyId),
                HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }
}
