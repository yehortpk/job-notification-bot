package com.github.yehortpk.subscriberbot.handlers.callback.back;

import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.callback.CallbackRequestHandlerImpl;

import java.util.List;

/**
 * Implementation of {@link BackCallbackRequestHandler} that reduce boilerplate code to simplify handlers appliances by
 * add common "/back" callback data to the <code>isApplicable</code> method. Requires <code>getExpectedStates</code> that
 * pass the {@link UserState} state to add it to requirements in handlers filtering
 */
public abstract class BackCallbackRequestHandlerImpl extends CallbackRequestHandlerImpl
        implements BackCallbackRequestHandler {
    @Override
    public boolean isApplicable(UserRequestDTO userRequest) {
        List<UserState> expectedStates = getExpectedStates();
        return super.isApplicable(userRequest) && expectedStates.contains(userRequest.getUser().getUserState());
    }

    @Override
    public String getExpectedData() {
        return "/back";
    }

    /**
     * Expected {@link UserState} states for <code>isApplicable</code> method in {@link BackCallbackRequestHandlerImpl}
     *
     * @return {@link List<UserState>} list of expected states
     */
    public abstract List<UserState> getExpectedStates();
}
