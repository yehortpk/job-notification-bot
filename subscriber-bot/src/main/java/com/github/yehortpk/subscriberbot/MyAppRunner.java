package com.github.yehortpk.subscriberbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class MyAppRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(MyAppRunner.class);

    @Autowired
    QuizCreatorBot quizCreatorBot;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            log.info("Registering bot...");
            log.info(quizCreatorBot.toString());
            telegramBotsApi.registerBot(quizCreatorBot);
        } catch (TelegramApiRequestException e) {
            log.error("Failed to register bot(check internet connection / bot token or make sure only one instance " +
                    "of bot is running).", e);
        }
        log.info("Telegram bot is ready to accept updates from user......");
    }
}
