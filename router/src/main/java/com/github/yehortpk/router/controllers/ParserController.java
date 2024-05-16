package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import com.github.yehortpk.router.services.NotifierService;
import com.github.yehortpk.router.services.VacancyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Controller that responsible for handling requests from KAFKA_PARSER_TOPIC from parser service
 */
@Component
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
@RequiredArgsConstructor
public class ParserController {
    private final VacancyService vacancyService;
    private final NotifierService notifierService;

    @KafkaListener(topics = {"#{environment['KAFKA_PARSER_TOPIC']}"}, containerFactory = "parserContainerFactory")
    @Transactional
    public void listenParserTopic(List<ConsumerRecord<String, VacancyDTO>> vacanciesBatch) {
        List<VacancyDTO> vacancies = vacanciesBatch.stream().map(ConsumerRecord::value).toList();

        vacancyService.addVacancies(vacancies);
//        vacancies.forEach(notifierService::notifyUsers);
    }
}
