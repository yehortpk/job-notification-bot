package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.callback.FilterInfoCallbackRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilterVacanciesListBackCallbackRequestHandler extends BackCallbackDataRequestHandlerImpl{
    private final FilterInfoCallbackRequestHandler filterInfoCallbackRequestHandler;

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
        return filterInfoCallbackRequestHandler;
    }

    @Override
    public UserState getPreviousUserState() {
        return UserState.FILTER_INFO_STATE;
    }
}
