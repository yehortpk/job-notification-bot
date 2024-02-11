package com.github.yehortpk.subscriberbot.handlers.callback.startmenu;

import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.callback.CallbackDataRequestHandlerImpl;

import java.util.Objects;

/**
 * Implementation for {@link StartMenuCallbackRequestHandler} that adds the START_STATE user state as a requirement
 * for handling
 */
public abstract class StartMenuCallbackDataRequestHandlerImpl extends CallbackDataRequestHandlerImpl
        implements StartMenuCallbackRequestHandler{
    @Override
    public boolean isApplicable(UserRequestDTO userRequest) {
        UserState userState = userRequest.getUser().getUserState();
        return super.isApplicable(userRequest) && Objects.equals(userState, UserState.START_STATE);
    }
}
