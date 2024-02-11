package com.github.yehortpk.subscriberbot.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import lombok.*;

import java.util.LinkedList;

/**
 * Model that responsible for store and transferring current user's state
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserDTO{
    /**
     * User-bot unique chatId
     */
    @JsonProperty("chat_id")
    private long chatId;
    /**
     * Current user state
     */
    @JsonProperty("user_state")
    private UserState userState;

    @JsonProperty("request_callback_data")
    private LinkedList<String> requestCallbackData = new LinkedList<>();

    public UserDTO(long chatId, UserState userState) {
        this.chatId = chatId;
        this.userState = userState;
    }

    public UserDAO toDAO() {
        return new UserDAO(chatId, userState, requestCallbackData);
    }

    public static UserDTO fromDAO(UserDAO dao) {
        return new UserDTO(dao.getChatId(), dao.getUserState(), dao.getRequestCallbackData());
    }

    public void addRequestCallbackData(String callbackData) {
        this.requestCallbackData.add(callbackData);
    }

    public String popLatestCallbackData() {
        return this.requestCallbackData.pollLast();
    }
}
