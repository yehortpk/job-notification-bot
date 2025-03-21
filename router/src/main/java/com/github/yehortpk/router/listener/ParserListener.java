package com.github.yehortpk.router.listener;

import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import com.github.yehortpk.router.services.NotifierService;
import com.github.yehortpk.router.services.VacancyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listener that responsible for handling requests from parser service
 */
@Component
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
@RequiredArgsConstructor
public class ParserListener {
    private final VacancyService vacancyService;
    private final NotifierService notifierService;

    @KafkaListener(topics = {"#{environment['KAFKA_PARSER_TOPIC']}"}, containerFactory = "parserContainerFactory")
    @Transactional
    public void listenParserTopic(VacancyDTO vacancy) {
        vacancyService.addVacancy(vacancy);

        if (notifierService.isNotifierEnabled()) {
            notifierService.notifyUsers(vacancy);
        }
    }
}
