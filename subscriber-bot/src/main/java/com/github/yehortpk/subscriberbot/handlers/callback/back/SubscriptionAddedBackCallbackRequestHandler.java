package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
import com.github.yehortpk.subscriberbot.handlers.message.commands.AddCommandRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionAddedBackCallbackRequestHandler extends BackCallbackDataRequestHandlerImpl{
    private final AddCommandRequestHandler addCommandRequestHandler;

    @Override
    public String getExpectedData() {
        return "back-subscription-added";
    }

    @Override
    public UserState getExpectedState() {
        return UserState.SUBSCRIPTION_ADDED_STATE;
    }

    @Override
    public RequestHandlerImpl getPreviousRequestHandler() {
        return addCommandRequestHandler;
    }

    @Override
    public UserState getPreviousUserState() {
        return UserState.ADD_SUBSCRIPTION_STATE;
    }
}
