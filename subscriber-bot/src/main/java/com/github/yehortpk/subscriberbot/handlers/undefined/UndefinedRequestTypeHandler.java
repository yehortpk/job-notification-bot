package com.github.yehortpk.subscriberbot.handlers.undefined;

import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.handlers.RequestHandler;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Handler responsible for shown a "Can't handle this type of request". Uses if no one is applicable
 */
@Component
public class UndefinedRequestTypeHandler extends RequestHandlerImpl implements RequestHandler {
    @Override
    public boolean isApplicable(UserRequestDTO userRequest) {
        return true;
    }

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        long chatId = userRequest.getUser().getChatId();

        String text = "I can't handle this type of message. Use buttons or commands (e.g /start)";
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
}
