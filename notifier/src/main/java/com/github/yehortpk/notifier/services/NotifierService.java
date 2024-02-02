package com.github.yehortpk.notifier.services;

import com.github.yehortpk.notifier.models.VacancyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class NotifierService {
    @Autowired
    KafkaTemplate<String, VacancyDTO> kafkaTemplate;

    public void notifyNewVacancies(Set<VacancyDTO> newVacancies) {
        for (VacancyDTO newVacancy : newVacancies) {
            kafkaTemplate.sendDefault(newVacancy);
        }
    }
}
