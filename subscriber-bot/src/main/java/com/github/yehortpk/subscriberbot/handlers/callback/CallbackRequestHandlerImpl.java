package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.handlers.StateRequestHandlerImpl;
import org.springframework.stereotype.Component;

@Component
public abstract class CallbackRequestHandlerImpl extends StateRequestHandlerImpl implements CallbackRequestHandler {
    @Override
    public boolean isApplicable(UserRequestDTO userRequest) {
        return super.isApplicable(userRequest) &&
                userRequest.getUpdate().hasCallbackQuery();
    }
}
