package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.callback.SubscriptionInfoCallbackRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FiltersListBackCallbackRequestHandler extends BackCallbackDataRequestHandlerImpl{
    private final SubscriptionInfoCallbackRequestHandler subscriptionInfoCallbackRequestHandler;

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
