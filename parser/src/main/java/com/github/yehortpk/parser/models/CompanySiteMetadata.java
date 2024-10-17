package com.github.yehortpk.parser.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CompanySiteMetadata {
    private int pagesCount;
    private Map<String, String> requestData;
    private Map<String, String> requestHeaders;
}
