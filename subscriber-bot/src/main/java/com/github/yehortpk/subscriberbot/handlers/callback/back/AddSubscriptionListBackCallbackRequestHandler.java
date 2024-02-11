package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.message.commands.StartCommandRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddSubscriptionListBackCallbackRequestHandler extends BackCallbackDataRequestHandlerImpl {
    @Autowired
    StartCommandRequestHandler startCommandRequestHandler;

    @Override
    public String getExpectedData() {
        return "back-add-subscription-list";
    }

    @Override
    public UserState getExpectedState() {
        return UserState.ADD_SUBSCRIPTION_STATE;
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
