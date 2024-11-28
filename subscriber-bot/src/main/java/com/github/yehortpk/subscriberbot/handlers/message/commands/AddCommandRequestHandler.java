package com.github.yehortpk.subscriberbot.handlers.message.commands;

import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.BackInlineMarkup;
import com.github.yehortpk.subscriberbot.services.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
public class AddCommandRequestHandler extends CommandRequestHandlerImpl {
    private final StateService stateService;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Type new filter below. Acceptable filter elements format:\n");
        stringBuilder.append("<b><i>Company name,</i></b>: no formatting, just simple keyword. This keyword has to be in vacancy" +
                " title, if no - vacancy not applicable. (Examples: Microsoft,; Simple Company,)\n");
        stringBuilder.append("<b><i>(keyword|keyword)</i></b>: binary choice. One of this keywords has to be in vacancy title, " +
                "if no - vacancy not applicable. Example: (developer|engineer)\n");
        stringBuilder.append("<b><i>keyword</i></b>: no formatting, just simple keyword. This keyword has to be in vacancy" +
                " title, if no - vacancy not applicable. (Example: java)\n");
        stringBuilder.append("<b><i>-keyword</i></b>: minus (or hyphen) before keyword. This keyword must not be in vacancy" +
                " title, if it is - vacancy not applicable. (Example: -lead -architect -qa\n");
        stringBuilder.append("Keywords can be combined. (Example: Google, (developer|engineer) software -senior -lead -qa " +
                "-automation -datascience -bigdata -android -kotlin -principal -consult -trainee -intern -devops\n");

        String callbackData = userRequest.getUpdate().getCallbackQuery().getData();

        UserDTO user = userRequest.getUser();
        user.setUserState(UserState.ADD_FILTER_STATE);
        user.addRequestCallbackData(callbackData);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(stringBuilder.toString())
                .replyMarkup(BackInlineMarkup.getMarkup("add-filter"))
                .build();
    }

    @Override
    public String getCommand() {
        return "/add";
    }
}
