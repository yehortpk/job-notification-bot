package com.github.yehortpk.subscriberbot.markups;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.List;


@Getter
enum StartButtons {
    ADD_SUBSCRIPTION_BUTTON("Add filter", "add-filter"),
    SUBSCRIPTIONS_LIST_BUTTON("Filters list", "filters");

    private final String text;
    private final String data;

    StartButtons(String text, String data) {
        this.text = text;
        this.data = data;
    }

}

/**
 * Main menu markup that has buttons from specified {@link StartButtons} enum
 */
public class MainMenuMarkup {
    public static InlineKeyboardMarkup getMarkup() {
        List<InlineKeyboardButton> buttons = Arrays.stream(StartButtons.values())
                .map((button) -> InlineKeyboardButton.builder()
                        .text(button.getText())
                        .callbackData(button.getData())
                        .build())
                .toList();


        return InlineKeyboardMarkup.builder()
                .clearKeyboard()
                .keyboard(
                    List.of(
                        buttons.subList(0, 2),
                        buttons.subList(2, buttons.size())
                    )
                )
                .build();
    }
}
