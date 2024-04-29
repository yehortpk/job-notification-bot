package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.VacancyShortDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.BackInlineMarkup;
import com.github.yehortpk.subscriberbot.services.StateService;
import com.github.yehortpk.subscriberbot.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionVacanciesCallbackRequestHandler extends CallbackDataRequestHandlerImpl{
    private final SubscriptionService subscriptionService;
    private final StateService stateService;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        String callbackData = userRequest.getUpdate().getCallbackQuery().getData();
        String[] companyData = callbackData.split("company=")[1].split(":");

        int companyId = Integer.parseInt(companyData[0]);
        String companyTitle = companyData[1];

        List<VacancyShortDTO> companyVacancies = subscriptionService.getCompanyVacancies(companyId);

        String messageText;
        InlineKeyboardMarkup messageMarkup = BackInlineMarkup.getMarkup("back-subscription-vacancies");
        if (!companyVacancies.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();

            String message = String.format("Vacancies for %s company: \n", companyTitle);
            stringBuilder.append(message);

            for (int i = 0; i < companyVacancies.size(); i++) {
                VacancyShortDTO vacancy = companyVacancies.get(i);
                stringBuilder.append(String.format("<a href=\"%s\">%s. %s</a>\n",
                        vacancy.getVacancyURL(), i + 1, vacancy.getVacancyTitle()));
            }

            messageText = stringBuilder.toString();
        } else {
            messageText = "There is no vacancies for this company";
        }

        UserDTO user = userRequest.getUser();

        user.setUserState(UserState.FILTERS_LIST_STATE);
        user.addRequestCallbackData(callbackData);
        stateService.saveUser(user);

        long chatId = user.getChatId();
        return SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .replyMarkup(messageMarkup)
                .build();
    }

    @Override
    public UserState getExpectedState() {
        return UserState.SUBSCRIPTION_INFO_STATE;
    }

    @Override
    public String getExpectedData() {
        return "vacancies";
    }
}
