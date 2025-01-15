package com.github.yehortpk.parser.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanySiteMetadata {
    private int pagesCount = 1;
    private Map<String, String> requestData = new HashMap<>();
    private Map<String, String> requestHeaders = new HashMap<>();
}
