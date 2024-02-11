package com.github.yehortpk.subscriberbot.markups;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class BackInlineMarkup {
    public static InlineKeyboardButton getButton(String backCallbackData) {
        return InlineKeyboardButton.builder()
                .text("<< Back")
                .callbackData("back-" + backCallbackData)
                .build();
    }

    public static InlineKeyboardMarkup getMarkup(String backCallbackData) {
        return new InlineKeyboardMarkup(List.of(List.of(getButton(backCallbackData))));
    }
}
