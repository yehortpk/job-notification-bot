package com.github.yehortpk.subscriberbot.handlers.message.commands;

import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.handlers.RequestHandlerImpl;
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
public abstract class CommandRequestHandlerImpl extends RequestHandlerImpl implements CommandRequestHandler{
    @Override
    public boolean isApplicable(UserRequestDTO userRequest) {
        String command = getCommand();
        return Objects.equals(userRequest.getUpdate().getMessage().getText(), command);
    }

    /**
     * Specify the command for <code>isApplicable</code> method filtering
     *
     * @return command
     */
    public abstract String getCommand();
}
