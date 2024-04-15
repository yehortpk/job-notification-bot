package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.models.VacancyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CompanyService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${company-url}")
    private String companyURL;

    public List<CompanyDTO> getCompaniesList() {
        ParameterizedTypeReference<List<CompanyDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(companyURL, HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }

    public List<VacancyDTO> getVacancies(int companyId) {
        ParameterizedTypeReference<List<VacancyDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        String finalURL = companyURL + "/%s/vacancies".formatted(companyId);

        return restTemplate.exchange(finalURL, HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }
}
