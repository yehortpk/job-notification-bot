package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.models.VacancyDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final RestTemplate restTemplate;

    @Value("${company-service-url}")
    private String companyServiceURL;

    @Value("${router-vacancies-url}")
    private String vacancyServiceURL;

    /**
     * Returns all companies list from router company service
     * @return companies list
     */
    public List<CompanyDTO> getCompaniesList() {
        ParameterizedTypeReference<List<CompanyDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        List<CompanyDTO> companies = restTemplate.exchange(companyServiceURL, HttpMethod.GET, null, parameterizedTypeReference).getBody();

        if (companies == null) {
            return new ArrayList<>();
        }
        return companies.stream().filter(CompanyDTO::isEnabled).toList();
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

    public Map<Long, Set<String>> getPersistedVacanciesUrlsByCompanyId() {
        ParameterizedTypeReference<Map<Long, Set<String>>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        String finalURL = vacancyServiceURL + "?byCompany=true";

        return restTemplate.exchange(finalURL, HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }
}
