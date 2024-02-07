package com.github.yehortpk.subscriberbot.handlers.message.commands;

import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.utils.TelegramServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Implementation of {@link CommandRequestHandler}. Reduces boilerplate code in command handlers <code>isApplicable</code>
 * method to simply specify the command in <code>getCommand</code> method. Also decorate handlers
 * <code>handleRequest</code> method to perform additional actions in every command handler.
 */
@Component
public abstract class CommandRequestHandlerImpl implements CommandRequestHandler{
    @Autowired
    TelegramServiceUtil telegramServiceUtil;

    @Override
    public boolean isApplicable(UserRequestDTO userRequest) {
        String command = getCommand();
        return Objects.equals(userRequest.getUpdate().getMessage().getText(), command);
    }

    /**
     * Handle user request. Method is separated by <code>handle</code> in common {@link CommandRequestHandlerImpl}
     * class for all command handlers and <code>handleRequest</code> for every specific handler.
     *
     * @param userRequest {@link UserRequestDTO} user request
     */
    @Override
    public void handle(UserRequestDTO userRequest){
        long chatId = userRequest.getUser().getChatId();
        int previousMessageId = userRequest.getUpdate().getMessage().getMessageId() - 1;

        telegramServiceUtil.removeMarkup(chatId, previousMessageId);

        // handle request by extended classes
        handleRequest(userRequest);
    }

    /**
     * Specify the command for <code>isApplicable</code> method filtering
     *
     * @return command
     */
    public abstract String getCommand();

    /**
     * Handle user request.
     *
     * @param userRequest {@link UserRequestDTO} request from user
     */
    public abstract void handleRequest(UserRequestDTO userRequest);
}
