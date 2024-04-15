package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.client.Client;
import com.github.yehortpk.router.models.company.Company;
import com.github.yehortpk.router.models.company.CompanyShortInfoDTO;
import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.models.filter.FilterDTO;
import com.github.yehortpk.router.models.filter.FilterShortInfoDTO;
import com.github.yehortpk.router.models.subscription.SubscriptionDTO;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.models.vacancy.VacancyShortDTO;
import com.github.yehortpk.router.repositories.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SubscribeService {
    private final ModelMapper modelMapper;
    private final ClientRepository clientRepository;
    private final CompanyService companyService;
    private final FilterService filterService;
    private final NotifierService notifierService;
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

    public List<CompanyShortInfoDTO> getSubscriptions(long chatId) {
        Client client = clientRepository.findById(chatId)
                .orElseGet(() -> {
                    Client newClient = new Client(chatId, new ArrayList<>());
                    clientRepository.save(newClient);
                    return  newClient;
                });


        return client.getSubscriptions().stream().map((company) ->
                modelMapper.map(company, CompanyShortInfoDTO.class)).toList();
    }

    public List<FilterShortInfoDTO> getFilters(long chatId, long companyId) {
        List<Filter> filters = filterService.findByCompanyIdAndClientId(companyId, chatId);
        return filters.stream().map((filter) -> modelMapper.map(filter, FilterShortInfoDTO.class)).toList();
    }

    public FilterShortInfoDTO getFilter(long filterId) {
        Optional<Filter> filter = filterService.findById(filterId);
        return modelMapper.map(filter.orElseThrow(), FilterShortInfoDTO.class);
    }

    public void deleteFilter(long filterId) {
        filterService.deleteFilter(filterId);
    }

    public void deleteSubscription(long chatId, long companyId) {
        Company company = companyService.findCompanyById(companyId);
        Client client = clientRepository.findById(chatId).orElseThrow();
        company.removeSubscription(client);
        companyService.saveCompany(company);
    }

    public void addFilter(FilterDTO filter) {
        filterService.addRawFilter(filter.getCompanyId(), filter.getClientId(), filter.getFilter());
    }

    public List<VacancyShortDTO> getVacanciesByFilter(long filterId) {
        Filter filter = filterService.findById(filterId).orElseThrow();
        List<Vacancy> allVacancies = vacancyService.getAllVacancies();
        // todo change to query
        // todo extract method from service
        List<VacancyShortDTO> result = new ArrayList<>();
        for (Vacancy vacancy : allVacancies) {
            if(notifierService.isVacancyApplicable(vacancy.getTitle(), filter.getFilter())) {
                result.add(modelMapper.map(vacancy, VacancyShortDTO.class));
            }
        }

        return result;
    }
}
