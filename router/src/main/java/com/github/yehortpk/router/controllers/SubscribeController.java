package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.SubscriptionDTO;
import com.github.yehortpk.router.services.SubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SubscribeController {
    @Autowired
    private SubscribeService subscribeService;

    @KafkaListener(topics = {"#{environment['KAFKA_SUBSCRIBE_TOPIC']}"}, containerFactory = "subscribeContainerFactory")
    public void listenSubscribeTopic(SubscriptionDTO subscription) {
        subscribeService.addSubscription(subscription);
    }
}