package com.github.yehortpk.subscriberbot.handlers;

import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.utils.TelegramServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.ArrayList;
import java.util.List;

@Component
public abstract class RequestHandlerImpl implements RequestHandler {
    @Autowired
    TelegramServiceUtil telegramServiceUtil;

    private final int TG_MESSAGE_MAX_LENGTH = 4000;

    // todo remove previous message
//    @Override
//    public void handle(UserRequestDTO userRequest) {
//        long chatId = userRequest.getUser().getChatId();
//        Message previousMessage = userRequest.getUpdate().getMessage().getReplyToMessage();
//        if (previousMessage.hasReplyMarkup()) {
//            telegramServiceUtil.deleteMessage(chatId, previousMessage.getMessageId());
//        }
//
//        SendMessage sendMessage = handleRequest(userRequest);
//        int messageId = userRequest.getUpdate().getMessage().getMessageId();
//
//        telegramServiceUtil.sendReplyMessageWithMarkup(chatId, sendMessage.getText(),
//                sendMessage.getReplyMarkup(), messageId);
//    }

    @Override
    public void handle(UserRequestDTO userRequest) {
        SendMessage sendMessage = handleRequest(userRequest);
        int chatId = Integer.parseInt(sendMessage.getChatId());
        String text = sendMessage.getText();
        ReplyKeyboard markup = sendMessage.getReplyMarkup();

        if(text.length() > TG_MESSAGE_MAX_LENGTH) {
            List<String> dividedLargeText = getDividedLargeText(text);
            for (String textPart : dividedLargeText) {
                telegramServiceUtil.sendMessageWithoutMarkup(chatId, textPart);
            }
            telegramServiceUtil.sendMessageWithMarkup(chatId, "", markup);
        } else {
            telegramServiceUtil.sendMessageWithMarkup(chatId, text, markup);
        }

    }

    // We need to find last close </a> tag to not crash tag parsing
    // All the residuals from string after the tag are going to the next message
    // todo check by TG_MESSAGE_MAX_LENGTH, it could be bigger
    private List<String> getDividedLargeText(String text) {
        List<String> result = new ArrayList<>();
        int residuals = 0;
        System.out.println("Text length: " + text.length());
        System.out.println("Parts count = " + (text.length() / TG_MESSAGE_MAX_LENGTH + 1));
        for (int i = 0; i < (text.length() / TG_MESSAGE_MAX_LENGTH); i++) {
            int lowerBoundIndex = i * TG_MESSAGE_MAX_LENGTH - residuals;

            int upperBoundIndex = (i + 1) * TG_MESSAGE_MAX_LENGTH;

            String rawSubstring = text.substring(i * TG_MESSAGE_MAX_LENGTH, upperBoundIndex);
            int lastClosedTagIndex = rawSubstring.lastIndexOf("</a>");
            int closedTagIndex = i * TG_MESSAGE_MAX_LENGTH + (
                    lastClosedTagIndex == -1?
                            TG_MESSAGE_MAX_LENGTH:
                            lastClosedTagIndex + 4
            );

            String taggedSubstring = text.substring(
                    lowerBoundIndex,
                    closedTagIndex
            );

            residuals = (i + 1) * TG_MESSAGE_MAX_LENGTH - closedTagIndex;

            System.out.printf("Part %s: %s, [%s-%s] residuals: %s%n", i + 1, taggedSubstring.length(), lowerBoundIndex, closedTagIndex, residuals);
//            System.out.printf("Part %s%n: %s%n", i + 1, taggedSubstring);
            result.add(taggedSubstring);
        }

        int lastPartLowerBoundIndex = text.length() - (text.length() % TG_MESSAGE_MAX_LENGTH + residuals);
        String lastSubstring = text.substring(
                lastPartLowerBoundIndex
        );
        result.add(lastSubstring);

        System.out.printf("Last part: length: %s%n", lastSubstring.length());

        return result;
    }

    public abstract SendMessage handleRequest(UserRequestDTO userRequest);
}
