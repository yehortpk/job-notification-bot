package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.callback.FiltersListCallbackRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterInfoBackCallbackRequestHandler extends BackCallbackDataRequestHandlerImpl{
    @Autowired
    FiltersListCallbackRequestHandler filtersListCallbackRequestHandler;

    @Override
    public UserState getExpectedState() {
        return UserState.FILTER_INFO_STATE;
    }

    @Override
    public RequestHandlerImpl getPreviousRequestHandler() {
        return filtersListCallbackRequestHandler;
    }

    @Override
    public UserState getPreviousUserState() {
        return UserState.FILTERS_LIST_STATE;
    }

    @Override
    public String getExpectedData() {
        return "back-filter-info";
    }
}
