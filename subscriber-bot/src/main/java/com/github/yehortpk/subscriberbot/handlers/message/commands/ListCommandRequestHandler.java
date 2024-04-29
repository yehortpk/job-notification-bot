package com.github.yehortpk.subscriberbot.handlers.message.commands;

import com.github.yehortpk.subscriberbot.dtos.CompanyShortInfoDTO;
import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.CompaniesListMenuMarkup;
import com.github.yehortpk.subscriberbot.services.StateService;
import com.github.yehortpk.subscriberbot.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListCommandRequestHandler extends CommandRequestHandlerImpl {
    private final SubscriptionService subscriptionService;
    private final StateService stateService;

    @Override
    public String getCommand() {
        return "/list";
    }

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        UserDTO user = userRequest.getUser();
        long chatId = user.getChatId();
        List<CompanyShortInfoDTO> subscriptions = subscriptionService.getSubscriptions(chatId);

        InlineKeyboardMarkup markup;
        String text;
        if(!subscriptions.isEmpty()) {
            markup = CompaniesListMenuMarkup.getMarkup(subscriptions, "subscriptions-list");
            text = "Your subscriptions:";
        } else {
            markup = null;
            text = "You don't have any subscriptions yet.";
        }

        user.setUserState(UserState.SUBSCRIPTION_LIST_STATE);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();
    }
}
