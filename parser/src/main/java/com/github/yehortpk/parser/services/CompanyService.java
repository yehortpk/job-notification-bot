package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.models.VacancyDTO;
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
    private RestTemplate restTemplate;

    @Value("${company-service-url}")
    private String companyServiceURL;

    /**
     * Returns all companies list from router company service
     * @return companies list
     */
    public List<CompanyDTO> getCompaniesList() {
        ParameterizedTypeReference<List<CompanyDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(companyServiceURL, HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }

    /**
     * Returns persisted vacancies from specific company
     * @param companyId id of company
     * @return list of persisted vacancies
     */
    public List<VacancyDTO> getPersistedVacancies(int companyId) {
        ParameterizedTypeReference<List<VacancyDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        String finalURL = companyServiceURL + "/%s/vacancies".formatted(companyId);

        return restTemplate.exchange(finalURL, HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }
}
