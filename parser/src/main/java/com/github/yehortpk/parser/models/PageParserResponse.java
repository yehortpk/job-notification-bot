package com.github.yehortpk.parser.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class PageParserResponse {
    Map<String, String> headers;
    Map<String, String> cookies;
    String body;
}
