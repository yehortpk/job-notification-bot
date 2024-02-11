package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.callback.SubscriptionInfoCallbackRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FiltersListBackCallbackRequestHandler extends BackCallbackDataRequestHandlerImpl{
    @Autowired
    SubscriptionInfoCallbackRequestHandler subscriptionInfoCallbackRequestHandler;

    @Override
    public UserState getExpectedState() {
        return UserState.FILTERS_LIST_STATE;
    }

    @Override
    public RequestHandlerImpl getPreviousRequestHandler() {
        return subscriptionInfoCallbackRequestHandler;
    }

    @Override
    public UserState getPreviousUserState() {
        return UserState.SUBSCRIPTION_INFO_STATE;
    }

    @Override
    public String getExpectedData() {
        return "back-filter-list";
    }
}
