package com.github.yehortpk.subscriberbot.handlers.message.commands;

import com.github.yehortpk.subscriberbot.models.CompanyShortInfoDTO;
import com.github.yehortpk.subscriberbot.models.UserDTO;
import com.github.yehortpk.subscriberbot.models.UserRequestDTO;
import com.github.yehortpk.subscriberbot.models.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.CompaniesListMenuMarkup;
import com.github.yehortpk.subscriberbot.services.CompanyService;
import com.github.yehortpk.subscriberbot.services.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompaniesCommandRequestHandler extends CommandRequestHandlerImpl {
    private final CompanyService companyService;
    private final StateService stateService;

    @Override
    public String getCommand() {
        return "/companies";
    }

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        UserDTO user = userRequest.getUser();
        long chatId = user.getChatId();

        List<CompanyShortInfoDTO> companies = companyService.getCompaniesList();

        InlineKeyboardMarkup markup;
        String text;
        if(!companies.isEmpty()) {
            markup = CompaniesListMenuMarkup.getMarkup(companies, "back-companies-list");
            text = "Show vacancies from company: \n";
        } else {
            markup = null;
            text = "There is no parsed vacancies.";
        }

        user.setUserState(UserState.COMPANIES_LIST_STATE);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();
    }
}
