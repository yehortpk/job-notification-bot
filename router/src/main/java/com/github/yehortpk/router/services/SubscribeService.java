package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.client.Client;
import com.github.yehortpk.router.models.company.Company;
import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.models.filter.FilterDTO;
import com.github.yehortpk.router.models.subscription.SubscriptionDTO;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.repositories.ClientRepository;
import com.github.yehortpk.router.utils.FilterParser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * This service provides methods for control subscriptions
 */
@Service
@RequiredArgsConstructor
public class SubscribeService {
    private final ClientRepository clientRepository;
    private final CompanyService companyService;
    private final FilterService filterService;
    private final VacancyService vacancyService;

    @Transactional
    public void addSubscription(SubscriptionDTO subscription) {
        long chatId = subscription.getChatId();
        Client client = clientRepository.findById(chatId)
                .orElseGet(() -> {
                    Client newClient = new Client(chatId, new ArrayList<>());
                    clientRepository.save(newClient);
                    return  newClient;
                });
        Company company = companyService.findCompanyById(subscription.getCompanyId());

        company.addSubscription(client);
        companyService.saveCompany(company);
    }

    public List<Company> getSubscriptions(long chatId) {
        Client client = clientRepository.findById(chatId)
                .orElseGet(() -> {
                    Client newClient = new Client(chatId, new ArrayList<>());
                    clientRepository.save(newClient);
                    return  newClient;
                });


        return client.getSubscriptions();
    }

    public List<Filter> getFilters(long chatId, long companyId) {
        return filterService.findByCompanyIdAndClientId(companyId, chatId);
    }

    @Transactional
    public void deleteFilter(long filterId) {
        filterService.deleteFilter(filterId);
    }

    @Transactional
    public void deleteSubscription(long chatId, long companyId) {
        Company company = companyService.findCompanyById(companyId);
        Client client = clientRepository.findById(chatId).orElseThrow();
        company.removeSubscription(client);
        companyService.saveCompany(company);
    }

    @Transactional
    public void addFilter(FilterDTO filter) {
        filterService.addRawFilter(filter.getCompanyId(), filter.getClientId(), filter.getFilter());
    }

    /**
     * Returns vacancies that pass the filter
     * @param filterId id of filter in database
     * @return list of filtered vacancies
     */
    public List<Vacancy> getVacanciesByFilter(long filterId) {
        Filter filter = filterService.findById(filterId).orElseThrow();
        List<Vacancy> allVacancies = vacancyService.getAllVacancies();
        // todo change to query
        // todo extract method from service
        List<Vacancy> result = new ArrayList<>();
        for (Vacancy vacancy : allVacancies) {
            FilterParser filterParser = new FilterParser(filter.getFilter());
            if (filterParser.isVacancyApplicable(vacancy.getTitle())) {
                result.add(vacancy);
            }
        }

        return result;
    }
}
