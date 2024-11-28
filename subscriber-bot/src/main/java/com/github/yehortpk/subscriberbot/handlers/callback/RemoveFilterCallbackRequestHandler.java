package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.BackInlineMarkup;
import com.github.yehortpk.subscriberbot.services.StateService;
import com.github.yehortpk.subscriberbot.services.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class RemoveFilterCallbackRequestHandler extends CallbackDataRequestHandlerImpl{
    private final StateService stateService;
    private final FilterService filterService;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        Update update = userRequest.getUpdate();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackQueryData = callbackQuery.getData();
        int filterId = Integer.parseInt(callbackQueryData.split("remove=")[1]);
        UserDTO user = userRequest.getUser();
        long chatId = user.getChatId();

        filterService.deleteFilter(filterId);

        user.setUserState(UserState.FILTER_REMOVED_STATE);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text("Filter was removed")
                .replyMarkup(BackInlineMarkup.getMarkup("remove-filter"))
                .build();
    }

    @Override
    public String getExpectedData() {
        return "remove";
    }

    @Override
    public UserState getExpectedState() {
        return UserState.FILTER_INFO_STATE;
    }
}
