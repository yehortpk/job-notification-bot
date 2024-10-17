package com.github.yehortpk.parser.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ScrapperResponseDTO {
    Map<String, String> headers;
    String body;
}
