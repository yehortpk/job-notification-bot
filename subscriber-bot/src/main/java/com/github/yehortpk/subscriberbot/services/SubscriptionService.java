package com.github.yehortpk.subscriberbot.services;

import com.github.yehortpk.subscriberbot.dtos.*;
import com.github.yehortpk.subscriberbot.utils.TelegramServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {
    private final TelegramServiceUtil telegramServiceUtil;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${subscription-url}")
    private String subscriptionURL;

    @Value("${filter-url}")
    private String filterURL;

    @Value("${company-url}")
    private String companyURL;

    public void addSubscription(long chatId, long companyId) {
        try {
            SubscriptionDTO subscription = new SubscriptionDTO(companyId, chatId);

            restTemplate.postForEntity(subscriptionURL, subscription, Void.class);
        } catch (RestClientException e) {
            log.error(e.getMessage());
            telegramServiceUtil.sendMessageWithoutMarkup(chatId, "Error occurred while adding");
        }
    }

    public List<CompanyShortInfoDTO> getSubscriptions(long chatId) {
        // Create a UriComponentsBuilder to build the complete URL with path parameter
        ParameterizedTypeReference<List<CompanyShortInfoDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(subscriptionURL + "/" + chatId,
                HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }

    public List<CompanyShortInfoDTO> getCompaniesList() {
        ParameterizedTypeReference<List<CompanyShortInfoDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(companyURL,
                HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }

    public List<FilterShortInfoDTO> getCompanyFilterList(long chatId, int companyId) {
        String finalUrl = filterURL + String.format("/%s/%s", chatId, companyId);

        ParameterizedTypeReference<List<FilterShortInfoDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(finalUrl,
                HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }

    public FilterShortInfoDTO getFilter(int filterId) {
        String finalURL =  filterURL+ "/" + filterId;
        return restTemplate.getForEntity(finalURL, FilterShortInfoDTO.class).getBody();
    }

    public void deleteFilter(int filterId) {
        String finalURL = filterURL + "/" + filterId;
        restTemplate.delete(finalURL);
    }

    public void deleteSubscription(long chatId, int companyId) {
        String finalUrl = subscriptionURL + String.format("/%s/%s", chatId, companyId);
        restTemplate.delete(finalUrl);
    }

    // todo validation
    public void addFilter(long chatId, int companyId, String filter) {
        restTemplate.postForEntity(filterURL, new FilterShortDTO(companyId, chatId, filter), Void.class);
    }

    public List<VacancyShortDTO> getCompanyVacancies(int companyId) {
        String finalURL =  companyURL + "/" + companyId + "/vacancies/";

        ParameterizedTypeReference<List<VacancyShortDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(finalURL,
                HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }

    public List<VacancyShortDTO> getCompanyVacancies(int companyId, int filterId) {
        String baseUrl =  companyURL + "/" + companyId + "/vacancies/";

        ParameterizedTypeReference<List<VacancyShortDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("filter_id", String.valueOf(filterId));

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        return restTemplate.exchange(builder.toUriString(),
                HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }

    public List<VacancyShortDTO> getVacanciesByFilter(int filterId) {
        String finalURL =  filterURL + "/vacancies/" + filterId;

        ParameterizedTypeReference<List<VacancyShortDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(finalURL,
                HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }
}
