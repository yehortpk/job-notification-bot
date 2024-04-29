package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.FilterShortInfoDTO;
import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.FilterInfoMenuMarkup;
import com.github.yehortpk.subscriberbot.services.StateService;
import com.github.yehortpk.subscriberbot.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class SubscriptionFilterInfoCallbackRequestHandler extends CallbackDataRequestHandlerImpl{
    private final StateService stateService;
    private final SubscriptionService subscriptionService;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        String callbackData = userRequest.getUpdate().getCallbackQuery().getData();
        String[] filterDetails = callbackData.split("filter_info=")[1].split(":");

        int filterId = Integer.parseInt(filterDetails[0]);
        int companyId = Integer.parseInt(filterDetails[1]);

        UserDTO user = userRequest.getUser();
        long chatId = user.getChatId();

        FilterShortInfoDTO filter = subscriptionService.getFilter(filterId);

        InlineKeyboardMarkup markup = FilterInfoMenuMarkup.getMarkup(filterId, companyId);

        user.setUserState(UserState.FILTER_INFO_STATE);
        user.addRequestCallbackData(callbackData);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text(filter.getFilter())
                .replyMarkup(markup)
                .build();
    }

    @Override
    public String getExpectedData() {
        return "filter_info";
    }

    @Override
    public UserState getExpectedState() {
        return UserState.FILTERS_LIST_STATE;
    }
}
