package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.*;
import com.github.yehortpk.router.repositories.CompanyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotifierService {
    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    KafkaTemplate<String, VacancyNotificationDTO> kafkaTemplate;

    @Transactional
    public void notifyUsers(VacancyDTO vacancy) {
        CompanyDTO company = CompanyDTO.fromDAO(companyRepository.findByCompanyId(vacancy.getCompanyID()));
        company.getSubscribers().forEach((subscriber) -> {
            VacancyNotificationDTO vacancyNotification = VacancyNotificationDTO.builder()
                    .chatId(subscriber.getChatId())
                    .vacancyTitle(vacancy.getTitle())
                    .companyTitle(company.getTitle())
                    .maxSalary(vacancy.getMaxSalary())
                    .minSalary(vacancy.getMinSalary())
                    .link(vacancy.getLink())
                    .build();

            kafkaTemplate.sendDefault(vacancyNotification);
        });
    }
}
