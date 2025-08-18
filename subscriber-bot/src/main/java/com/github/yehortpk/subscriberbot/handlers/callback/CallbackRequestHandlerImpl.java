package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.models.UserRequestDTO;
import com.github.yehortpk.subscriberbot.handlers.StateRequestHandlerImpl;
import org.springframework.stereotype.Component;

@Component
public abstract class CallbackRequestHandlerImpl extends StateRequestHandlerImpl implements CallbackRequestHandler {
    @Override
    public void handle(UserRequestDTO userRequest) {
        long chatId = userRequest.getUser().getChatId();
        Integer messageId = userRequest.getUpdate().getCallbackQuery().getMessage().getMessageId();

        super.handle(userRequest);
        telegramServiceUtil.deleteMessage(chatId, messageId);
    }

    @Override
    public boolean isApplicable(UserRequestDTO userRequest) {
        return super.isApplicable(userRequest) &&
                userRequest.getUpdate().hasCallbackQuery();
    }
}
