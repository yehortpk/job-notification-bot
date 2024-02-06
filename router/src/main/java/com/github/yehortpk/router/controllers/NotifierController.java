package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.VacancyDTO;
import com.github.yehortpk.router.services.NotifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotifierController {
    @Autowired
    private NotifierService notifierService;

    @KafkaListener(topics = {"#{environment['KAFKA_NOTIFIER_TOPIC']}"}, containerFactory = "notifierContainerFactory")
    public void listenNotifierTopic(VacancyDTO vacancy) {
        notifierService.notifyUsers(vacancy);
    }
}
