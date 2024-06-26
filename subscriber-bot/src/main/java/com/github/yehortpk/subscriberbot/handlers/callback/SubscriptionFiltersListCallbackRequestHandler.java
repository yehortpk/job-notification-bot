package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.FilterShortInfoDTO;
import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.FiltersListMenuMarkup;
import com.github.yehortpk.subscriberbot.services.StateService;
import com.github.yehortpk.subscriberbot.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionFiltersListCallbackRequestHandler extends CallbackDataRequestHandlerImpl {
    private final SubscriptionService subscriptionService;
    private final StateService stateService;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        String callbackData = userRequest.getUpdate().getCallbackQuery().getData();
        String[] companyData = callbackData.split("filters=")[1].split(":");

        UserDTO user = userRequest.getUser();
        long chatId = user.getChatId();

        int companyId = Integer.parseInt(companyData[0]);
        String companyTitle = companyData[1];

        List<FilterShortInfoDTO> companyFilterList = subscriptionService.getCompanyFilterList(chatId, companyId);

        String messageText;
        InlineKeyboardMarkup messageMarkup;
        if (!companyFilterList.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();

            String message = String.format("Your filters for %s company: \n", companyTitle);
            stringBuilder.append(message);

            List<String[]> filters = new ArrayList<>();
            for (int i = 0; i < companyFilterList.size(); i++) {
                FilterShortInfoDTO filterDTO = companyFilterList.get(i);
                stringBuilder.append(String.format("%s. %s\n", i + 1, filterDTO.getFilter()));
                filters.add(new String[]{
                        String.valueOf(i + 1),
                        String.valueOf(filterDTO.getFilterId()),
                        filterDTO.getFilter()
                });
            }

            messageText = stringBuilder.toString();
            messageMarkup = FiltersListMenuMarkup.getMarkup(filters, companyId);
        } else {
            messageText = "You don't have any filters yet";
            messageMarkup = FiltersListMenuMarkup.getMarkup(new ArrayList<>(), companyId);
        }

        user.setUserState(UserState.FILTERS_LIST_STATE);
        user.addRequestCallbackData(callbackData);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .replyMarkup(messageMarkup)
                .build();
    }

    @Override
    public String getExpectedData() {
        return "filters";
    }

    @Override
    public UserState getExpectedState() {
        return UserState.SUBSCRIPTION_INFO_STATE;
    }
}
