package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.models.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.message.commands.StartCommandRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FiltersListBackCallbackRequestHandler extends BackCallbackDataRequestHandlerImpl{
    private final StartCommandRequestHandler startCommandRequestHandler;

    @Override
    public UserState getExpectedState() {
        return UserState.FILTERS_LIST_STATE;
    }

    @Override
    public RequestHandlerImpl getPreviousRequestHandler() {
        return startCommandRequestHandler;
    }

    @Override
    public UserState getPreviousUserState() {
        return UserState.START_STATE;
    }

    @Override
    public String getExpectedData() {
        return "back-filter-list";
    }
}
