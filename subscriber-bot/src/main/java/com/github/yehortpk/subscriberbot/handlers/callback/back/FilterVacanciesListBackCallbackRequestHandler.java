package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.callback.SubscriptionFilterInfoCallbackRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterVacanciesListBackCallbackRequestHandler extends BackCallbackDataRequestHandlerImpl{
    @Autowired
    SubscriptionFilterInfoCallbackRequestHandler subscriptionFilterInfoCallbackRequestHandler;

    @Override
    public UserState getExpectedState() {
        return UserState.FILTER_VACANCIES_LIST;
    }

    @Override
    public String getExpectedData() {
        return "filter-vacancies";
    }

    @Override
    public RequestHandlerImpl getPreviousRequestHandler() {
        return subscriptionFilterInfoCallbackRequestHandler;
    }

    @Override
    public UserState getPreviousUserState() {
        return UserState.FILTER_INFO_STATE;
    }
}
