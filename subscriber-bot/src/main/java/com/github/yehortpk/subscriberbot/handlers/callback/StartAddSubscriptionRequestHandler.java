package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.message.commands.AddCommandRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class StartAddSubscriptionRequestHandler extends CallbackDataRequestHandlerImpl{
    @Autowired
    AddCommandRequestHandler addCommandRequestHandler;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        return addCommandRequestHandler.handleRequest(userRequest);
    }

    @Override
    public String getExpectedData() {
        return "add-subscription";
    }

    @Override
    public UserState getExpectedState() {
        return UserState.START_STATE;
    }
}
