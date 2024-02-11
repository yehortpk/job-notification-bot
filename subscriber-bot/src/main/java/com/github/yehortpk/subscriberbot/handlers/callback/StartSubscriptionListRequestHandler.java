package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.message.commands.ListCommandRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class StartSubscriptionListRequestHandler extends CallbackDataRequestHandlerImpl{
    @Autowired
    ListCommandRequestHandler listCommandRequestHandler;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        return listCommandRequestHandler.handleRequest(userRequest);
    }

    @Override
    public String getExpectedData() {
        return "subscriptions-list";
    }

    @Override
    public UserState getExpectedState() {
        return UserState.START_STATE;
    }
}
