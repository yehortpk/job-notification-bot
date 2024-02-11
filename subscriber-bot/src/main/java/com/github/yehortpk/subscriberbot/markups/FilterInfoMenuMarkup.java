package com.github.yehortpk.subscriberbot.markups;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class FilterInfoMenuMarkup {
    public static InlineKeyboardMarkup getMarkup(int filterId, int companyId) {
        InlineKeyboardButton removeFilterButton = InlineKeyboardButton.builder()
                .text("Remove filter")
                .callbackData("remove=" + filterId)
                .build();

        InlineKeyboardButton vacanciesButton = InlineKeyboardButton.builder()
                .text("Vacancies list by filter")
                .callbackData(String.format("vacancies_list=%s:%s", filterId, companyId))
                .build();

        InlineKeyboardButton backButton = BackInlineMarkup.getButton("filter-info");

        return InlineKeyboardMarkup.builder()
                .clearKeyboard()
                .keyboard(List.of(
                        List.of(removeFilterButton),
                        List.of(vacanciesButton),
                        List.of(backButton)))
                .build();
    }
}
