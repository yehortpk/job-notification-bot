package com.github.yehortpk.parser.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO representing {@link Document object} itself and its metadata
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO {
    private String pageURL;
    private Map<String, String> pageData = new HashMap<>();
    private Map<String, String> pageHeaders = new HashMap<>();
    private Document doc;
}
