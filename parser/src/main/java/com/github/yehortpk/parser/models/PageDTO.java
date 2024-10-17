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
    private int pageId;
    private Map<String, String> pageData = new HashMap<>();
    private Document doc;

    public PageDTO(String pageURL, int pageId, Document doc) {
        this.pageURL = pageURL;
        this.pageId = pageId;
        this.doc = doc;
    }
}
