package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.callback.SubscriptionFiltersListCallbackRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddFilterBackCallbackRequestHandler extends BackCallbackDataRequestHandlerImpl{
    @Autowired
    SubscriptionFiltersListCallbackRequestHandler subscriptionFiltersListCallbackRequestHandler;

    @Override
    public String getExpectedData() {
        return "back-add-filter";
    }

    @Override
    public UserState getExpectedState() {
        return UserState.ADD_FILTER_STATE;
    }

    @Override
    public RequestHandlerImpl getPreviousRequestHandler() {
        return subscriptionFiltersListCallbackRequestHandler;
    }

    @Override
    public UserState getPreviousUserState() {
        return UserState.FILTERS_LIST_STATE;
    }
}
