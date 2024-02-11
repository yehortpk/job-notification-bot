package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Implementation of {@link CallbackRequestHandler} interface. Reduces boilerplate code for CallbackRequest handlers
 * by provide a common <code>isApplicable</code> method that requires only <code>getExpectedData</code> data for
 * handler by the callback data dispatching
 */
@Component
public abstract class CallbackDataRequestHandlerImpl extends CallbackRequestHandlerImpl {

    @Override
    public boolean isApplicable(UserRequestDTO userRequest) {
        Update update = userRequest.getUpdate();
        String expectedData = getExpectedData();
        return super.isApplicable(userRequest) &&
                update.getCallbackQuery().getData().contains(expectedData);
    }

    /**
     * Specify the data for <code>isApplicable</code> method
     *
     * @return Expected data
     */
    public abstract String getExpectedData();
}
