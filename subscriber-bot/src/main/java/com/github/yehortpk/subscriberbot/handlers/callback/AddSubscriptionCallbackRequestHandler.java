package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.BackInlineMarkup;
import com.github.yehortpk.subscriberbot.services.StateService;
import com.github.yehortpk.subscriberbot.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
public class AddSubscriptionCallbackRequestHandler extends CallbackDataRequestHandlerImpl{
    private final SubscriptionService subscriptionService;
    private final StateService stateService;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        String callbackData = userRequest.getUpdate().getCallbackQuery().getData();
        String[] companyInfo = callbackData
                .split("company=")[1].split(":");

        int companyId = Integer.parseInt(companyInfo[0]);
        String companyTitle = companyInfo[1];
        long chatId = userRequest.getUser().getChatId();

        subscriptionService.addSubscription(chatId, companyId);
        UserDTO user = userRequest.getUser();
        user.addRequestCallbackData(callbackData);
        user.setUserState(UserState.SUBSCRIPTION_ADDED_STATE);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text(String.format("Subscription to %s company has been added", companyTitle))
                .replyMarkup(BackInlineMarkup.getMarkup("subscription-added"))
                .build();
    }

    @Override
    public String getExpectedData() {
        return "company";
    }

    @Override
    public UserState getExpectedState() {
        return UserState.ADD_SUBSCRIPTION_STATE;
    }
}
