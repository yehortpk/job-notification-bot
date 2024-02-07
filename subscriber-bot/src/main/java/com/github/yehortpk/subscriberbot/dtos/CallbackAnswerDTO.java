package com.github.yehortpk.subscriberbot.dtos;

import lombok.*;

/**
 * Model responsible for external service communication. Represents user's chosen answer to the question
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CallbackAnswerDTO {
    private long chatId;
    private int choseAnswerIndex;
}
