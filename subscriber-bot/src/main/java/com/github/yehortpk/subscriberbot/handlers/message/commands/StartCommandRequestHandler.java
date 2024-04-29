package com.github.yehortpk.subscriberbot.handlers.message.commands;

import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.MainMenuMarkup;
import com.github.yehortpk.subscriberbot.services.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
public class StartCommandRequestHandler extends CommandRequestHandlerImpl{
    private final StateService stateService;

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        UserDTO user = userRequest.getUser();
        user.setUserState(UserState.START_STATE);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(userRequest.getUser().getChatId())
                .text("I can help you create and manage subscriptions to vacancies.")
                .replyMarkup(MainMenuMarkup.getMarkup())
                .build();
    }
}
