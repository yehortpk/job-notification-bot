package com.github.yehortpk.subscriberbot.services;

import com.github.yehortpk.subscriberbot.models.*;
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
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${filter-url}")
    private String filterURL;

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

    public VacanciesPageDTO getVacanciesByFilter(int filterId, int pageId) {
        String finalURL =  filterURL + String.format("/%s/vacancies?page=%s", filterId, pageId);

        return restTemplate.getForEntity(finalURL, VacanciesPageDTO.class).getBody();
    }
}
