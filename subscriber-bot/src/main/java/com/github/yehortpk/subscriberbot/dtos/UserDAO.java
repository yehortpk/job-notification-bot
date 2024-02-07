package com.github.yehortpk.subscriberbot.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * Model that responsible for store and transferring current user's state
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@RedisHash("user_state")
public class UserDAO {
    /**
     * User-bot unique chatId
     */
    @JsonProperty("chat_id")
    @Id
    private long chatId;
    /**
     * Current user state
     */
    @JsonProperty("user_state")
    private UserState userState;
}
