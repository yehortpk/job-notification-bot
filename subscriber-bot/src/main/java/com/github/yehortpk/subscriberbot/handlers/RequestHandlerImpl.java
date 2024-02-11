package com.github.yehortpk.subscriberbot.handlers;

import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.utils.TelegramServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Component
public abstract class RequestHandlerImpl implements RequestHandler {
    @Autowired
    TelegramServiceUtil telegramServiceUtil;

    // todo remove previous message
//    @Override
//    public void handle(UserRequestDTO userRequest) {
//        long chatId = userRequest.getUser().getChatId();
//        Message previousMessage = userRequest.getUpdate().getMessage().getReplyToMessage();
//        if (previousMessage.hasReplyMarkup()) {
//            telegramServiceUtil.deleteMessage(chatId, previousMessage.getMessageId());
//        }
//
//        SendMessage sendMessage = handleRequest(userRequest);
//        int messageId = userRequest.getUpdate().getMessage().getMessageId();
//
//        telegramServiceUtil.sendReplyMessageWithMarkup(chatId, sendMessage.getText(),
//                sendMessage.getReplyMarkup(), messageId);
//    }

    @Override
    public void handle(UserRequestDTO userRequest) {
        SendMessage sendMessage = handleRequest(userRequest);
        int chatId = Integer.parseInt(sendMessage.getChatId());
        String text = sendMessage.getText();
        ReplyKeyboard markup = sendMessage.getReplyMarkup();

        telegramServiceUtil.sendMessageWithMarkup(chatId, text, markup);
    }

    public abstract SendMessage handleRequest(UserRequestDTO userRequest);
}
