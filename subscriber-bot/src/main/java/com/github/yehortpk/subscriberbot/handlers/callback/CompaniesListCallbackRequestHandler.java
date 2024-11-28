package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.message.commands.CompaniesCommandRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
public class CompaniesListCallbackRequestHandler extends CallbackDataRequestHandlerImpl{
    private final CompaniesCommandRequestHandler companiesCommandRequestHandler;

    @Override
    public String getExpectedData() {
        return "companies-list";
    }

    @Override
    public UserState getExpectedState() {
        return UserState.START_STATE;
    }

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        return companiesCommandRequestHandler.handleRequest(userRequest);
    }
}
