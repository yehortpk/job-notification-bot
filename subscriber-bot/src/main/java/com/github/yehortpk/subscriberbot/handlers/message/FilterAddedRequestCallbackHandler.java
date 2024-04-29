package com.github.yehortpk.subscriberbot.handlers.message;

import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.StateRequestHandlerImpl;
import com.github.yehortpk.subscriberbot.markups.BackInlineMarkup;
import com.github.yehortpk.subscriberbot.services.StateService;
import com.github.yehortpk.subscriberbot.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
public class FilterAddedRequestCallbackHandler extends StateRequestHandlerImpl implements MessageRequestHandler {
    private final StateService stateService;
    private final SubscriptionService subscriptionService;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        long chatId = userRequest.getUser().getChatId();

        UserDTO user = stateService.getUser(chatId);
        String latestCallbackData = user.popLatestCallbackData();
        int companyId = Integer.parseInt(latestCallbackData.split("add-filter=")[1]);
        String filter = userRequest.getUpdate().getMessage().getText();

        subscriptionService.addFilter(chatId, companyId, filter);

        user.setUserState(UserState.FILTER_ADDED_STATE);
        user.addRequestCallbackData(latestCallbackData);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text("Filter has been added")
                .replyMarkup(BackInlineMarkup.getMarkup("filter-added"))
                .build();
    }

    @Override
    public UserState getExpectedState() {
        return UserState.ADD_FILTER_STATE;
    }
}
