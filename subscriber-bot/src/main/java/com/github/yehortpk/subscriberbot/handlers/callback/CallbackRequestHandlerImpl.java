package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.handlers.StateRequestHandlerImpl;
import com.github.yehortpk.subscriberbot.utils.TelegramServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class CallbackRequestHandlerImpl extends StateRequestHandlerImpl implements CallbackRequestHandler {
    @Autowired
    private TelegramServiceUtil telegramServiceUtil;

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
