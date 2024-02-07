package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.utils.TelegramServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

/**
 * Implementation of {@link CallbackRequestHandler} interface. Reduces boilerplate code for CallbackRequest handlers
 * by provide a common <code>isApplicable</code> method that requires only <code>getExpectedData</code> data for
 * handler by the callback data dispatching
 */
@Component
public abstract class CallbackRequestHandlerImpl implements CallbackRequestHandler {
    @Autowired
    TelegramServiceUtil telegramServiceUtil;

    @Override
    public boolean isApplicable(UserRequestDTO userRequest) {
        Update update = userRequest.getUpdate();
        String expectedData = getExpectedData();
        return update.hasCallbackQuery() && Objects.equals(update.getCallbackQuery().getData(), expectedData);
    }

    /**
     * Handle user request. Method is separated by <code>handle</code> in common {@link CallbackRequestHandlerImpl}
     * class for all command handlers and <code>handleRequest</code> for every specific handler.
     *
     * @param userRequest {@link UserRequestDTO} user request
     */
    @Override
    public void handle(UserRequestDTO userRequest){
        long chatId = userRequest.getUser().getChatId();
        int messageId = userRequest.getUpdate().getCallbackQuery().getMessage().getMessageId();

        telegramServiceUtil.deleteMessage(chatId, messageId);

        // handle request by extended classes
        handleRequest(userRequest);
    }

    /**
     * Specify the data for <code>isApplicable</code> method
     *
     * @return Expected data
     */
    public abstract String getExpectedData();

    /**
     * Handle user request
     *
     * @param userRequest {@link UserRequestDTO} user request
     */
    public abstract void handleRequest(UserRequestDTO userRequest);
}
