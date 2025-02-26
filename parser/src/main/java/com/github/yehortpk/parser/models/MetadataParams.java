package com.github.yehortpk.parser.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MetadataParams {
    private int pagesCount;
    private Map<String, String> requestData;
    private Map<String, String> requestHeaders;
    private Map<String, String> requestCookies;
}
