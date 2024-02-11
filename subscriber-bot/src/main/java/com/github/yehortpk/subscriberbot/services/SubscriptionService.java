package com.github.yehortpk.subscriberbot.services;

import com.github.yehortpk.subscriberbot.dtos.*;
import com.github.yehortpk.subscriberbot.utils.TelegramServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
// todo change from router service to a new one with rest
public class SubscriptionService {
    @Autowired
    TelegramServiceUtil telegramServiceUtil;

    RestTemplate restTemplate = new RestTemplate();

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
            e.printStackTrace();
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
        List<FilterShortInfoDTO> subscriptions = restTemplate.exchange(finalUrl,
                HttpMethod.GET, null, parameterizedTypeReference).getBody();

        return subscriptions;
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

    public List<VacancyShortDTO> getVacanciesByFilter(int filterId) {
        String finalURL =  filterURL+ "/vacancies/" + filterId;

        ParameterizedTypeReference<List<VacancyShortDTO>> parameterizedTypeReference
                = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(finalURL,
                HttpMethod.GET, null, parameterizedTypeReference).getBody();
    }
}
