package com.github.yehortpk.subscriberbot.markups;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class SubscriptionInfoMenuMarkup {
    public static InlineKeyboardMarkup getMarkup(int companyId, String companyTitle) {
        InlineKeyboardButton vacanciesButton = InlineKeyboardButton.builder()
                .text("Company vacancies")
                .callbackData(String.format("company=%s:%s", companyId, companyTitle))
                .build();

        InlineKeyboardButton filtersButton = InlineKeyboardButton.builder()
                .text("Filters")
                .callbackData(String.format("filters=%s:%s", companyId, companyTitle))
                .build();

        InlineKeyboardButton stopSubscriptionButton = InlineKeyboardButton.builder()
                .text("Remove subscription")
                .callbackData(String.format("remove=%s", companyId))
                .build();

        List<List<InlineKeyboardButton>> buttons =
                List.of(
                        List.of(filtersButton),
                        List.of(stopSubscriptionButton),
                        List.of(BackInlineMarkup.getButton("subscription-info"))
                );


        return InlineKeyboardMarkup.builder()
                .clearKeyboard()
                .keyboard(buttons)
                .build();
    }
}
