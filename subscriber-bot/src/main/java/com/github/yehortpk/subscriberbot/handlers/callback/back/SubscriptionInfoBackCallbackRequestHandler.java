package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.message.commands.ListCommandRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionInfoBackCallbackRequestHandler extends BackCallbackDataRequestHandlerImpl{
    @Autowired
    ListCommandRequestHandler listCommandRequestHandler;

    @Override
    public UserState getExpectedState() {
        return UserState.SUBSCRIPTION_INFO_STATE;
    }

    @Override
    public RequestHandlerImpl getPreviousRequestHandler() {
        return listCommandRequestHandler;
    }

    @Override
    public UserState getPreviousUserState() {
        return UserState.SUBSCRIPTION_LIST_STATE;
    }

    @Override
    public String getExpectedData() {
        return "back-subscription-info";
    }
}
