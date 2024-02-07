package com.github.yehortpk.subscriberbot.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Internal model that encapsulate two types - {@link UserDTO} and {@link Update}. {@link UserDTO} is responsible
 * for storing and transferring user state, and {@link Update} is user request from bot
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequestDTO {
    private UserDTO user;
    private Update update;
}
