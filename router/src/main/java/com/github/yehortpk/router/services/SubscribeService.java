package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.client.Client;
import com.github.yehortpk.router.models.client.ClientDTO;
import com.github.yehortpk.router.models.company.Company;
import com.github.yehortpk.router.models.company.CompanyShortInfoDTO;
import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.models.filter.FilterDTO;
import com.github.yehortpk.router.models.filter.FilterShortDTO;
import com.github.yehortpk.router.models.filter.FilterShortInfoDTO;
import com.github.yehortpk.router.models.subscription.SubscriptionDTO;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import com.github.yehortpk.router.models.vacancy.VacancyShortDTO;
import com.github.yehortpk.router.repositories.ClientRepository;
import com.github.yehortpk.router.repositories.CompanyRepository;
import com.github.yehortpk.router.repositories.FilterRepository;
import com.github.yehortpk.router.repositories.VacancyRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SubscribeService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    FilterRepository filterRepository;

    @Autowired
    NotifierService notifierService;

    @Autowired
    VacancyRepository vacancyRepository;

    @Transactional
    public void addSubscription(SubscriptionDTO subscription) {
        long chatId = subscription.getChatId();
        Client client = clientRepository.findById(chatId)
                .orElseGet(() -> {
                    Client newClient = new Client(chatId, new ArrayList<>());
                    clientRepository.save(newClient);
                    return  newClient;
                });
        Company company = companyRepository.findById(subscription.getCompanyId()).orElseThrow();

        company.addSubscription(client);
        companyRepository.save(company);
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
        List<Filter> filters = filterRepository.findByCompanyIdAndClientId(companyId, chatId);
        return filters.stream().map((filter) -> modelMapper.map(filter, FilterShortInfoDTO.class)).toList();
    }

    public FilterShortInfoDTO getFilter(long filterId) {
        Optional<Filter> filter = filterRepository.findById(filterId);
        return modelMapper.map(filter.orElseThrow(), FilterShortInfoDTO.class);
    }

    public void deleteFilter(long filterId) {
        filterRepository.deleteById(filterId);
    }

    public void deleteSubscription(long chatId, long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow();
        Client client = clientRepository.findById(chatId).orElseThrow();
        company.removeSubscription(client);
        companyRepository.save(company);
    }

    public void addFilter(FilterShortDTO filter) {
        Company company = companyRepository.findById(filter.getCompanyId()).orElseThrow();
        Client client = clientRepository.findById(filter.getClientId()).orElseThrow();
        filterRepository.save(new Filter(company, client, filter.getFilter()));
    }

    public List<VacancyShortDTO> getVacanciesByFilter(long filterId) {
        Filter filter = filterRepository.findById(filterId).orElseThrow();
        List<Vacancy> allVacancies = vacancyRepository.findAll();
        // todo change to query
        // todo extract method from service
        List<VacancyShortDTO> result = new ArrayList<>();
        for (Vacancy vacancy : allVacancies) {
            if(notifierService.isApplicable(vacancy.getTitle(), filter.getFilter())) {
                result.add(modelMapper.map(vacancy, VacancyShortDTO.class));
            }
        }

        return result;
    }
}
