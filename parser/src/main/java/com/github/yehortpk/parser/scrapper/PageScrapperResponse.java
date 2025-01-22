package com.github.yehortpk.parser.scrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class PageScrapperResponse {
    private Map<String, String> headers;
    private Map<String, String> cookies;
    private String body;
}
