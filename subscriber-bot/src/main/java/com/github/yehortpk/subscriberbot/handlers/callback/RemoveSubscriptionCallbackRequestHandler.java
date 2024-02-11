package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.BackInlineMarkup;
import com.github.yehortpk.subscriberbot.services.StateService;
import com.github.yehortpk.subscriberbot.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class RemoveSubscriptionCallbackRequestHandler extends CallbackDataRequestHandlerImpl{
    @Autowired
    StateService stateService;

    @Autowired
    SubscriptionService subscriptionService;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        int companyId = Integer.parseInt(userRequest.getUpdate().getCallbackQuery().getData()
                .split("remove=")[1]);

        long chatId = userRequest.getUser().getChatId();

        subscriptionService.deleteSubscription(chatId, companyId);

        UserDTO user = userRequest.getUser();
        user.setUserState(UserState.SUBSCRIPTION_REMOVED_STATE);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text("Subscription was removed")
                .replyMarkup(BackInlineMarkup.getMarkup("remove-subscription"))
                .build();
    }

    @Override
    public String getExpectedData() {
        return "remove";
    }

    @Override
    public UserState getExpectedState() {
        return UserState.SUBSCRIPTION_INFO_STATE;
    }
}
