package com.github.yehortpk.subscriberbot;

import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.exceptions.MissedHandlerException;
import com.github.yehortpk.subscriberbot.handlers.Dispatcher;
import com.github.yehortpk.subscriberbot.handlers.RequestHandler;
import com.github.yehortpk.subscriberbot.handlers.callback.CallbackRequestHandler;
import com.github.yehortpk.subscriberbot.handlers.message.MessageRequestHandler;
import com.github.yehortpk.subscriberbot.handlers.undefined.UndefinedRequestTypeHandler;
import com.github.yehortpk.subscriberbot.services.StateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramBot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Telegram API bot for notifications about a new vacancy. Based on long polling queries (extends {@link TelegramLongPollingBot}).
 * Main purpose of the class to receive and handle users' {@link Update} updates, using {@link RequestHandler} handlers
 */
@Component
@Slf4j
public class QuizCreatorBot extends TelegramLongPollingBot implements TelegramBot {
    private final StateService stateService;
    private final ApplicationContext applicationContext;

    @Value("${TG_BOT_USERNAME}")
    private String botUsername;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Autowired
    public QuizCreatorBot(@Value("${TG_BOT_API_KEY}") String apiKey, StateService stateService, ApplicationContext applicationContext) {
        super(apiKey);
        this.stateService = stateService;
        this.applicationContext = applicationContext;
    }

    /**
     * Handles incoming {@link Update} update, select appropriate handlers by message type, and execute them through
     * {@link Dispatcher}. At the end saves {@link UserDTO} user object state update to backend database
     *
     * @param update Update received
     */
    @Override
    public void onUpdateReceived(Update update) {
        // All the beans, extended from RequestHandler are checked to see if the Dispatcher can apply update to them
        Map<String, ? extends RequestHandler> beanMap;
        long chatId;
        if (update.hasMessage()) {
            // Handlers that work with plain messages or commands
            beanMap = applicationContext.getBeansOfType(MessageRequestHandler.class);
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            // Handlers that work with callbacks
            beanMap = applicationContext.getBeansOfType(CallbackRequestHandler.class);
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            throw new MissedHandlerException("Handler is missed");
        }
        UserDTO user = getUserByChatId(chatId);

        List<RequestHandler> handlersList = new ArrayList<>(beanMap.values());
        // If no one is applicable - run undefined request handler
        handlersList.add(applicationContext.getBean(UndefinedRequestTypeHandler.class));
        Dispatcher dispatcher = new Dispatcher(handlersList, user);
        dispatcher.dispatch(update);
    }

    /**
     * Return {@link UserDTO} user instance by chatId from backend database
     *
     * @param chatId Bot-user chat id
     * @return {@link UserDTO} user instance
     */
    private UserDTO getUserByChatId(long chatId) {
        UserDTO user = stateService.getUser(chatId);
        if (user.getChatId() == 0) {
            user = new UserDTO(chatId, UserState.START_STATE);
        }

        return user;
    }

    @Override
    public void clearWebhook() {}
}
