package com.github.yehortpk.subscriberbot.handlers.message.commands;

import com.github.yehortpk.subscriberbot.dtos.CompanyShortInfoDTO;
import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.BackInlineMarkup;
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
public class AddCommandRequestHandler extends CommandRequestHandlerImpl{
    private final SubscriptionService subscriptionService;
    private final StateService stateService;

    @Override
    public String getCommand() {
        return "/add";
    }

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        UserDTO user = userRequest.getUser();
        long chatId = user.getChatId();
        List<CompanyShortInfoDTO> companies = subscriptionService.getCompaniesList();
        List<CompanyShortInfoDTO> subscriptions = subscriptionService.getSubscriptions(chatId);

        List<CompanyShortInfoDTO> difference = companies.stream().filter(
                (subscription) -> !subscriptions.contains(subscription)).toList();

        user.setUserState(UserState.ADD_SUBSCRIPTION_STATE);
        stateService.saveUser(user);

        String text;
        InlineKeyboardMarkup markup;

        String backButtonText = "add-subscription-list";
        if(!difference.isEmpty()) {
            text = "Available subscriptions: ";
            markup = CompaniesListMenuMarkup.getMarkup(difference, backButtonText);
        } else {
            text = "You have already subscribed to all available companies";
            markup = BackInlineMarkup.getMarkup(backButtonText);
        }

        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();
    }
}
