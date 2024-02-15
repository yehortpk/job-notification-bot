package com.github.yehortpk.subscriberbot.controllers;

import com.github.yehortpk.subscriberbot.dtos.VacancyNotificationDTO;
import com.github.yehortpk.subscriberbot.services.NotifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
@Component
public class NotificationController {
    @Autowired
    NotifierService notifierService;

    @KafkaListener(topics = {"#{environment['KAFKA_BOT_NOTIFIER_TOPIC']}"},
            containerFactory = "botNotifierContainerFactory")
    public void listenBotNotifierTopic(VacancyNotificationDTO vacancy) {
        notifierService.notifyUser(vacancy);
    }
}
