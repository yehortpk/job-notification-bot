package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.message.commands.CompaniesCommandRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyVacanciesListBackCallbackRequestHandler extends BackCallbackDataRequestHandlerImpl{
    private final CompaniesCommandRequestHandler companiesCommandRequestHandler;

    @Override
    public UserState getExpectedState() {
        return UserState.COMPANY_VACANCIES_LIST_STATE;
    }

    @Override
    public String getExpectedData() {
        return "back-company-vacancies";
    }

    @Override
    public RequestHandlerImpl getPreviousRequestHandler() {
        return companiesCommandRequestHandler;
    }

    @Override
    public UserState getPreviousUserState() {
        return UserState.COMPANIES_LIST_STATE;
    }
}
