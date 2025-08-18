package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.models.UserRequestDTO;
import com.github.yehortpk.subscriberbot.models.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.message.commands.ListCommandRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
public class FiltersListCallbackRequestHandler extends CallbackDataRequestHandlerImpl {
    private final ListCommandRequestHandler listCommandRequestHandler;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
       return listCommandRequestHandler.handleRequest(userRequest);
    }

    @Override
    public String getExpectedData() {
        return "filters";
    }

    @Override
    public UserState getExpectedState() {
        return UserState.START_STATE;
    }
}
