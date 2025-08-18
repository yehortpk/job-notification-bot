package com.github.yehortpk.subscriberbot.handlers.message;

import com.github.yehortpk.subscriberbot.models.UserDTO;
import com.github.yehortpk.subscriberbot.models.UserRequestDTO;
import com.github.yehortpk.subscriberbot.models.enums.UserState;
import com.github.yehortpk.subscriberbot.handlers.StateRequestHandlerImpl;
import com.github.yehortpk.subscriberbot.markups.BackInlineMarkup;
import com.github.yehortpk.subscriberbot.services.StateService;
import com.github.yehortpk.subscriberbot.services.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
public class FilterAddedRequestCallbackHandler extends StateRequestHandlerImpl implements MessageRequestHandler {
    private final StateService stateService;
    private final FilterService filterService;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        long chatId = userRequest.getUser().getChatId();

        UserDTO user = stateService.getUser(chatId);
        String latestCallbackData = user.popLatestCallbackData();
        String filter = userRequest.getUpdate().getMessage().getText();

        filterService.addFilter(chatId, filter);

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
