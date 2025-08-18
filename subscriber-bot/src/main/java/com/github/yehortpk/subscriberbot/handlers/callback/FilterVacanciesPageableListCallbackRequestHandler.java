package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.models.*;
import com.github.yehortpk.subscriberbot.models.enums.UserState;
import com.github.yehortpk.subscriberbot.services.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
public class FilterVacanciesPageableListCallbackRequestHandler extends CallbackDataRequestHandlerImpl{
    private final FilterVacanciesListCallbackRequestHandler handler;
    private final StateService stateService;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        String callbackData = userRequest.getUpdate().getCallbackQuery().getData();
        String[] filterDetails = callbackData
                .split("vacancies_list=")[1].split(":");

        int filterId = Integer.parseInt(filterDetails[0]);
        int pageId = Integer.parseInt(filterDetails[1]);

        SendMessage resMessage = handler.generateMessage(filterId, pageId, userRequest, callbackData);

        UserDTO user = userRequest.getUser();
        user.popLatestCallbackData();
        stateService.saveUser(user);

        return resMessage;
    }

    @Override
    public UserState getExpectedState() {
        return UserState.FILTER_VACANCIES_LIST_STATE;
    }

    @Override
    public String getExpectedData() {
        return "vacancies_list";
    }
}
