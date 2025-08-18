package com.github.yehortpk.subscriberbot.handlers;

import com.github.yehortpk.subscriberbot.models.UserRequestDTO;
import com.github.yehortpk.subscriberbot.models.enums.UserState;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public abstract class StateRequestHandlerImpl extends RequestHandlerImpl {
    @Override
    public boolean isApplicable(UserRequestDTO userRequest) {
        UserState userState = userRequest.getUser().getUserState();
        return Objects.equals(userState, getExpectedState());
    }

    public abstract UserState getExpectedState();
}
