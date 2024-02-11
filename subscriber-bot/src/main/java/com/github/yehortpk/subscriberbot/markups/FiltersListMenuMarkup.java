package com.github.yehortpk.subscriberbot.markups;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class FiltersListMenuMarkup {
    public static InlineKeyboardMarkup getMarkup(List<String[]> filters, int companyId) {
        List<InlineKeyboardButton> buttons = filters.stream()
                .map((filter) -> InlineKeyboardButton.builder()
                        .text(filter[0])
                        .callbackData(String.format("filter_info=%s:%s", filter[1], companyId))
                        .build())
                .toList();

        InlineKeyboardButton newFilterButton = InlineKeyboardButton.builder()
                .text("Add new filter")
                .callbackData(String.format("add-filter=%s", companyId))
                .build();

        return InlineKeyboardMarkup.builder()
                .clearKeyboard()
                .keyboard(
                        List.of(
                                buttons,
                                List.of(newFilterButton),
                                List.of(BackInlineMarkup.getButton("filter-list"))
                        )
                )
                .build();
    }
}
