package com.github.yehortpk.subscriberbot.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import lombok.*;

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

    public UserDAO toDAO() {
        return new UserDAO(chatId, userState);
    }

    public static UserDTO fromDAO(UserDAO dao) {
        return new UserDTO(dao.getChatId(), dao.getUserState());
    }
}
