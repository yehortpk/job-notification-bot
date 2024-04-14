package com.github.yehortpk.notifier.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.nodes.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO {
    private String pageURL;
    private int pageId;
    private Document doc;
}
