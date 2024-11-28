package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.callback.FiltersListCallbackRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilterInfoBackCallbackRequestHandler extends BackCallbackDataRequestHandlerImpl{
    private final FiltersListCallbackRequestHandler filtersListCallbackRequestHandler;

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
