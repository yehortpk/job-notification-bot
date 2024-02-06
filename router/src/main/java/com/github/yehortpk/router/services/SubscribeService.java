package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.ClientDAO;
import com.github.yehortpk.router.models.CompanyDAO;
import com.github.yehortpk.router.models.SubscriptionDTO;
import com.github.yehortpk.router.repositories.ClientRepository;
import com.github.yehortpk.router.repositories.CompanyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SubscribeService {
    @Autowired
    ClientRepository clientRepository;

    @Autowired
    CompanyRepository companyRepository;

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
}
