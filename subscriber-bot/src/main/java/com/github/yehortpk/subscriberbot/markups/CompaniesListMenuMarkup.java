package com.github.yehortpk.subscriberbot.markups;

import com.github.yehortpk.subscriberbot.dtos.CompanyShortInfoDTO;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class CompaniesListMenuMarkup {
    public static InlineKeyboardMarkup getMarkup(List<CompanyShortInfoDTO> companies, String backCallbackData) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (CompanyShortInfoDTO company : companies) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(company.getCompanyTitle())
                    .callbackData(String.format("company-vacancies=%s:%s", company.getCompanyId(), company.getCompanyTitle()))
                    .build();
            buttons.add(List.of(button));
        }

        buttons.add(List.of(BackInlineMarkup.getButton(backCallbackData)));

        return InlineKeyboardMarkup.builder()
                .clearKeyboard()
                .keyboard(buttons)
                .build();
    }
}
