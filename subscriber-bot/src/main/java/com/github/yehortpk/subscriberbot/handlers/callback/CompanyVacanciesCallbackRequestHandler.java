package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.models.UserDTO;
import com.github.yehortpk.subscriberbot.models.UserRequestDTO;
import com.github.yehortpk.subscriberbot.models.VacancyShortDTO;
import com.github.yehortpk.subscriberbot.models.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.BackInlineMarkup;
import com.github.yehortpk.subscriberbot.services.CompanyService;
import com.github.yehortpk.subscriberbot.services.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompanyVacanciesCallbackRequestHandler extends CallbackDataRequestHandlerImpl{
    private final CompanyService companyService;
    private final StateService stateService;

    @Override
    public String getExpectedData() {
        return "company-vacancies";
    }

    @Override
    public UserState getExpectedState() {
        return UserState.COMPANIES_LIST_STATE;
    }

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        String callbackData = userRequest.getUpdate().getCallbackQuery().getData();

        int company_id = Integer.parseInt(callbackData
                .split("company-vacancies=")[1].split(":")[0]);
        String companyTitle = callbackData
                .split("company-vacancies=")[1].split(":")[1];

        String text;
        List<VacancyShortDTO> vacancies = companyService.getCompanyVacancies(company_id);
        if (!vacancies.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(String.format("There are %s vacancies from %s:\n",
                    vacancies.size(), companyTitle));

            for (int i = 0; i < vacancies.size(); i++) {
                VacancyShortDTO vacancy = vacancies.get(i);
                stringBuilder.append(String.format("%s. <a href='%s'>%s</a>\n", i + 1, vacancy.getVacancyURL(),
                        vacancy.getVacancyTitle()));
            }

            text = stringBuilder.toString();
        } else {
            text = "There is no vacancies by this company";
        }

        UserDTO user = userRequest.getUser();
        user.setUserState(UserState.COMPANY_VACANCIES_LIST_STATE);
        user.addRequestCallbackData(callbackData);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(text)
                .replyMarkup(BackInlineMarkup.getMarkup("back-company-vacancies"))
                .build();
    }
}
