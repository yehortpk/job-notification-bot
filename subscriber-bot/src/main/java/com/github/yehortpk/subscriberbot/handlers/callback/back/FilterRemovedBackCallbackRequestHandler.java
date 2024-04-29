package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.callback.SubscriptionFiltersListCallbackRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilterRemovedBackCallbackRequestHandler extends BackCallbackDataRequestHandlerImpl{
    private final SubscriptionFiltersListCallbackRequestHandler subscriptionFiltersListCallbackRequestHandler;

    @Override
    public UserState getPreviousUserState() {
        return UserState.FILTERS_LIST_STATE;
    }

    @Override
    public UserState getExpectedState() {
        return UserState.FILTER_REMOVED_STATE;
    }

    @Override
    public RequestHandlerImpl getPreviousRequestHandler() {
        return subscriptionFiltersListCallbackRequestHandler;
    }

    @Override
    public String getExpectedData() {
        return "back-remove-filter";
    }
}
