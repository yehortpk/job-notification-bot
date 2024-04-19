package com.github.yehortpk.parser.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.nodes.Document;

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
    private Document doc;
}
