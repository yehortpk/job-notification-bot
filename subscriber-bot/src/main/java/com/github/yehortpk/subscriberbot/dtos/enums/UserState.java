package com.github.yehortpk.subscriberbot.dtos.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Current state of the user
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum UserState {
    START_STATE,
    ADD_SUBSCRIPTION_STATE,
    SUBSCRIPTION_LIST_STATE,
    SUBSCRIPTION_INFO_STATE,
    SUBSCRIPTION_REMOVED_STATE,
    SUBSCRIPTION_ADDED_STATE,
    ADD_FILTER_STATE,
    FILTERS_LIST_STATE,
    FILTER_INFO_STATE,
    FILTER_ADDED_STATE,
    FILTER_REMOVED_STATE,
    FILTER_VACANCIES_LIST,
}
