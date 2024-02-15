package com.github.yehortpk.subscriberbot.utils;

import com.github.yehortpk.subscriberbot.Executor;
import com.github.yehortpk.subscriberbot.QuizCreatorBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

/**
 * {@link TelegramServiceUtil} class created for simplify connection between code and TelegramAPI endpoints.
 * Uses {@link BotApiMethod} methods, wrapped by widely used common methods.
 */
@Component
public class TelegramServiceUtil {
    Logger log = LoggerFactory.getLogger(TelegramServiceUtil.class);

    private final Executor executor;

    public TelegramServiceUtil(QuizCreatorBot quizCreatorBot) {
        executor = new Executor(quizCreatorBot);
    }

    /**
     * Send message to user-bot chat with specific markup
     *
     * @param chatId Bot-user chatId
     * @param text   Message to send
     * @param markup {@link ReplyKeyboardMarkup} markup to send
     * @return {@link Message} message that was sent
     */
    public Message sendMessageWithMarkup(long chatId, String text, ReplyKeyboard markup) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build();

        if (markup != null) {
            sendMessage.setReplyMarkup(markup);
        }

        Message message = (Message) executor.execute(sendMessage);

        if (log.isDebugEnabled()) {
            log.debug(String.format("Sent message, ID=%s, text=%s", message.getMessageId(), message.getText()));
        }

        return message;
    }

    /**
     * Send message to user-bot chat without specific markup
     *
     * @param chatId Bot-user chatId
     * @param text   Message to send
     * @return {@link Message} message that was sent
     */
    public Message sendMessageWithoutMarkup(long chatId, String text) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build();

        Message message = (Message) executor.execute(sendMessage);

        if (log.isDebugEnabled()) {
            log.debug(String.format("Sent message, ID=%s, text=%s", message.getMessageId(), message.getText()));
        }

        return message;
    }

    /**
     * Delete specific message
     *
     * @param chatId    Bot-user chatId
     * @param messageId ID of message to deletion
     */
    public void deleteMessage(long chatId, int messageId) {
        DeleteMessage sendRemoveInfoMessage = DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();

        executor.execute(sendRemoveInfoMessage);

        if (log.isDebugEnabled()) {
            log.debug(String.format("Message deleted, ID=%s", messageId));
        }
    }

}
