package com.github.yehortpk.subscriberbot.handlers.message.commands;

import com.github.yehortpk.subscriberbot.dtos.FilterDTO;
import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.FiltersListMenuMarkup;
import com.github.yehortpk.subscriberbot.services.StateService;
import com.github.yehortpk.subscriberbot.services.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ListCommandRequestHandler extends CommandRequestHandlerImpl {
    private final FilterService filterService;
    private final StateService stateService;

    @Override
    public String getCommand() {
        return "/list";
    }

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        UserDTO user = userRequest.getUser();
        long chatId = user.getChatId();

        List<FilterDTO> filtersList = filterService.getFiltersList(chatId);

        String messageText;
        InlineKeyboardMarkup messageMarkup;
        if (!filtersList.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();

            String message = "Your filters: \n";
            stringBuilder.append(message);

            List<String[]> filters = new ArrayList<>();
            for (int i = 0; i < filtersList.size(); i++) {
                FilterDTO filterDTO = filtersList.get(i);
                stringBuilder.append(String.format("%s. %s\n", i + 1, filterDTO.getFilter()));
                filters.add(new String[]{
                        String.valueOf(i + 1),
                        String.valueOf(filterDTO.getFilterId()),
                        filterDTO.getFilter()
                });
            }

            messageText = stringBuilder.toString();
            messageMarkup = FiltersListMenuMarkup.getMarkup(filters);
        } else {
            messageText = "You don't have any filters yet";
            messageMarkup = FiltersListMenuMarkup.getMarkup(new ArrayList<>());
        }

        user.setUserState(UserState.FILTERS_LIST_STATE);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .replyMarkup(messageMarkup)
                .build();
    }
}
