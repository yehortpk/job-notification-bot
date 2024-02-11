package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.*;
import com.github.yehortpk.router.repositories.ClientRepository;
import com.github.yehortpk.router.repositories.CompanyRepository;
import com.github.yehortpk.router.repositories.FilterRepository;
import com.github.yehortpk.router.repositories.VacancyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SubscribeService {
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
        ClientDAO client = clientRepository.findById(chatId)
                .orElseGet(() -> {
                    ClientDAO newClient = new ClientDAO(chatId, new ArrayList<>());
                    clientRepository.save(newClient);
                    return  newClient;
                });
        CompanyDAO company = companyRepository.findById(subscription.getCompanyId()).orElseThrow();


        company.addSubscription(client);
        companyRepository.save(company);
    }

    public List<CompanyShortInfoDTO> getSubscriptions(long chatId) {
        ClientDAO clientDAO = clientRepository.findById(chatId)
                .orElseGet(() -> {
                    ClientDAO newClient = new ClientDAO(chatId, new ArrayList<>());
                    clientRepository.save(newClient);
                    return  newClient;
                });

        ClientDTO clientDTO = ClientDTO.fromDAO(clientDAO);

        return clientDTO.getSubscriptions().stream().map(CompanyShortInfoDTO::fromDTO).toList();
    }

    public List<FilterShortInfoDTO> getFilters(long chatId, long companyId) {
        List<FilterDAO> filtersDAO = filterRepository.findByCompanyIdAndClientId(companyId, chatId);
        return filtersDAO.stream().map(FilterShortInfoDTO::fromDAO).toList();
    }

    public FilterShortInfoDTO getFilter(long filterId) {
        Optional<FilterDAO> filer = filterRepository.findById(filterId);
        return FilterShortInfoDTO.fromDAO(filer.orElseThrow());
    }

    public void deleteFilter(long filterId) {
        filterRepository.deleteById(filterId);
    }

    public void deleteSubscription(long chatId, long companyId) {
        CompanyDAO companyDAO = companyRepository.findById(companyId).orElseThrow();
        ClientDAO clientDAO = clientRepository.findById(chatId).orElseThrow();
        companyDAO.removeSubscription(clientDAO);
        companyRepository.save(companyDAO);
    }

    public void addFilter(FilterShortDTO filter) {
        CompanyDAO companyDAO = companyRepository.findById(filter.getCompanyId()).orElseThrow();
        ClientDAO clientDAO = clientRepository.findById(filter.getClientId()).orElseThrow();
        filterRepository.save(new FilterDAO(companyDAO, clientDAO, filter.getFilter()));
    }

    public List<VacancyShortDTO> getVacanciesByFilter(long filterId) {
        FilterDTO filterDTO = FilterDTO.fromDAO(filterRepository.findById(filterId).orElseThrow());
        List<VacancyDAO> allVacancies = vacancyRepository.findAll();
        // todo change to query
        // todo extract method from service
        List<VacancyShortDTO> result = new ArrayList<>();
        for (VacancyDAO vacancy : allVacancies) {
            String vacancyTitle = vacancy.getTitle();
            if(notifierService.isApplicable(vacancyTitle, filterDTO.getFilter())) {
                String vacancyUrl = vacancy.getLink();
                int companyId = vacancy.getCompanyID();
                result.add(new VacancyShortDTO(companyId, vacancyTitle, vacancyUrl));
            }
        }

        return result;
    }
}
