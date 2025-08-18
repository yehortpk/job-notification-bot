package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.models.UserDTO;
import com.github.yehortpk.subscriberbot.models.UserRequestDTO;
import com.github.yehortpk.subscriberbot.models.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.callback.CallbackDataRequestHandlerImpl;
import com.github.yehortpk.subscriberbot.services.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Implementation of {@link BackCallbackRequestHandler} that reduce boilerplate code to simplify handlers appliances by
 * add common "/back" callback data to the <code>isApplicable</code> method. Requires <code>getExpectedStates</code> that
 * pass the {@link UserState} state to add it to requirements in handlers filtering
 */
@Component
public abstract class BackCallbackDataRequestHandlerImpl extends CallbackDataRequestHandlerImpl
        implements BackCallbackRequestHandler {
    @Autowired
    protected StateService stateService;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        UserDTO user = userRequest.getUser();
        user.popLatestCallbackData();
        String latestCallbackData = user.popLatestCallbackData();
        user.setUserState(getPreviousUserState());
        stateService.saveUser(user);

        userRequest.setUser(user);
        Update update = userRequest.getUpdate();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        callbackQuery.setData(latestCallbackData);
        userRequest.setUpdate(update);

        RequestHandlerImpl prevRequestHandler = getPreviousRequestHandler();
        return prevRequestHandler.handleRequest(userRequest);
    }

    public abstract RequestHandlerImpl getPreviousRequestHandler();
    public abstract UserState getPreviousUserState();
}
