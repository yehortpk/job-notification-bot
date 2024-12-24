package com.github.yehortpk.subscriberbot.markups;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class VacanciesListPageableMarkup {
    public static InlineKeyboardMarkup getMarkup(int filterId, int currentPage, int totalPages) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        if (currentPage < 1) {
            throw new RuntimeException("Current page parameter can't be less then 1");
        }

        if (currentPage > totalPages) {
            throw new RuntimeException("Current page parameter can't be greater then totalPages parameter");
        }

        if (currentPage == totalPages && currentPage == 1) {
            return null;
        }

        if( currentPage > 1 ) {
            InlineKeyboardButton prevBtn = new InlineKeyboardButton("<<");
            prevBtn.setCallbackData(generateCallbackData(filterId, currentPage - 1));
            buttons.add(prevBtn);
        }

        if( currentPage < totalPages ) {
            InlineKeyboardButton nextBtn = new InlineKeyboardButton(">>");
            nextBtn.setCallbackData(generateCallbackData(filterId, currentPage + 1));
            buttons.add(nextBtn);
        }


        return InlineKeyboardMarkup.builder()
                .clearKeyboard()
                .keyboard(List.of(buttons, List.of(BackInlineMarkup.getButton("back-filter-vacancies"))))
                .build();
    }

    protected static String generateCallbackData(int filterId, int pageId) {
        return String.format("vacancies_list=%s:%s", filterId, pageId);
    }
}
