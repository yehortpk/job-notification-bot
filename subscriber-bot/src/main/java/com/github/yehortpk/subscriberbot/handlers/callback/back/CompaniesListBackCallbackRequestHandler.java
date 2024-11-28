package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.message.commands.StartCommandRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompaniesListBackCallbackRequestHandler extends BackCallbackDataRequestHandlerImpl{
    private final StartCommandRequestHandler startCommandRequestHandler;

    @Override
    public UserState getExpectedState() {
        return UserState.COMPANIES_LIST_STATE;
    }

    @Override
    public String getExpectedData() {
        return "back-companies-list";
    }

    @Override
    public RequestHandlerImpl getPreviousRequestHandler() {
        return startCommandRequestHandler;
    }

    @Override
    public UserState getPreviousUserState() {
        return UserState.START_STATE;
    }
}
