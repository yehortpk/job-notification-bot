package com.github.yehortpk.subscriberbot;

import com.github.yehortpk.subscriberbot.exceptions.TelegramRuntimeException;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

/**
 * {@link Executor} is the class that takes a {@link DefaultAbsSender} bot and execute some {@link BotApiMethod}
 * Telegram Api method
 *
 */
public class Executor {
    private DefaultAbsSender bot;

    public Executor(DefaultAbsSender bot){
        this.bot = bot;
    }

    /**
     *
     * @param method {@link BotApiMethod} (Telegram API command)
     * @return the result of executing the {@link BotApiMethod} (Telegram API command)
     */
    public Serializable execute(BotApiMethod method) {
        try{
            return bot.execute(method);
        } catch (TelegramApiException e) {
            throw new TelegramRuntimeException(e.getMessage());
        }
    }
}
