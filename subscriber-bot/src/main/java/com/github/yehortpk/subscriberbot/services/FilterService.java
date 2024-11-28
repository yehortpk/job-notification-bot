package com.github.yehortpk.subscriberbot.services;

import com.github.yehortpk.subscriberbot.dtos.*;
import com.github.yehortpk.subscriberbot.utils.TelegramServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilterService {
    private final TelegramServiceUtil telegramServiceUtil;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${subscription-url}")
    private String subscriptionURL;

    @Value("${filter-url}")
    private String filterURL;

    @Value("${company-url}")
    private String companyURL;

    public List<CompanyShortInfoDTO> getCompaniesList() {
        ParameterizedTypeReference<List<CompanyShortInfoDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(companyURL,
                HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }

    public List<FilterDTO> getFiltersList(long chatId) {
        String finalUrl = filterURL + String.format("/chat/%s", chatId);

        ParameterizedTypeReference<List<FilterDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(finalUrl,
                HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }

    public FilterDTO getFilter(int filterId) {
        String finalURL =  filterURL+ "/" + filterId;
        return restTemplate.getForEntity(finalURL, FilterDTO.class).getBody();
    }

    public void deleteFilter(int filterId) {
        String finalURL = filterURL + "/" + filterId;
        restTemplate.delete(finalURL);
    }

    // todo validation
    public void addFilter(long chatId, String filter) {
        restTemplate.postForEntity(filterURL, new FilterDTO(0, chatId, filter), Void.class);
    }

    public List<VacancyShortDTO> getVacanciesByFilter(int filterId) {
        String finalURL =  filterURL + String.format("/%s/vacancies", filterId);

        ParameterizedTypeReference<List<VacancyShortDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(finalURL,
                HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }
}
