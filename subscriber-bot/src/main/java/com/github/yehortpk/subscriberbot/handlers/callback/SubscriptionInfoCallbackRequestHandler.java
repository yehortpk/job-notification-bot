package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.SubscriptionInfoMenuMarkup;
import com.github.yehortpk.subscriberbot.services.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class SubscriptionInfoCallbackRequestHandler extends CallbackDataRequestHandlerImpl {
    private final StateService stateService;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        String callbackData = userRequest.getUpdate().getCallbackQuery().getData();
        String[] companyData =
                callbackData.split("company=")[1].split(":");

        int companyId = Integer.parseInt(companyData[0]);
        String companyTitle = companyData[1];

        InlineKeyboardMarkup markup = SubscriptionInfoMenuMarkup.getMarkup(companyId, companyTitle);

        UserDTO user = userRequest.getUser();
        long chatId = user.getChatId();

        user.setUserState(UserState.SUBSCRIPTION_INFO_STATE);
        user.addRequestCallbackData(callbackData);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text(String.format("Choose option for %s company: ", companyTitle))
                .replyMarkup(markup)
                .build();
    }

    @Override
    public UserState getExpectedState() {
        return UserState.SUBSCRIPTION_LIST_STATE;
    }

    @Override
    public String getExpectedData() {
        return "company";
    }
}
