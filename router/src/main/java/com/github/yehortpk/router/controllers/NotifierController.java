package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import com.github.yehortpk.router.services.NotifierService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class NotifierController {
    @Autowired
    private NotifierService notifierService;

    @KafkaListener(topics = {"#{environment['KAFKA_NOTIFIER_TOPIC']}"}, containerFactory = "notifierContainerFactory")
    public void listenNotifierTopic(List<ConsumerRecord<String, VacancyDTO>> vacanciesBatch) {
        List<VacancyDTO> vacancies = vacanciesBatch.stream().map(ConsumerRecord::value).toList();
        System.out.println("Vacancies batch: " + vacancies);
        notifierService.addVacancies(vacancies);
//        vacancies.forEach(notifierService::notifyUsers);
    }
}
