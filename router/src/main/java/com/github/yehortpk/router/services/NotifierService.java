package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.ClientDTO;
import com.github.yehortpk.router.models.CompanyDTO;
import com.github.yehortpk.router.models.VacancyDTO;
import com.github.yehortpk.router.repositories.CompanyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotifierService {
    @Autowired
    CompanyRepository companyRepository;

    @Transactional
    public void notifyUsers(VacancyDTO vacancy) {
        List<CompanyDTO> companies = companyRepository.findByIsEnabledTrue()
                .stream()
                .map(CompanyDTO::fromDAO)
                .toList();

        for (CompanyDTO company : companies) {
            for (ClientDTO subscriber : company.getSubscribers()) {
                System.out.printf("New vacancy: %s for client: %s\n", vacancy, subscriber);
            }
        }
    }
}
