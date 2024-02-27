package com.github.yehortpk.notifier.entities.parsers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.Connection;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public abstract class PageParserImpl implements PageParser {
    protected String pageUrl;
    protected int pageId;
    protected Map<String, String> headers;
    protected Map<String, String> data;
    protected Connection.Method parseMethod = Connection.Method.GET;

    public PageParserImpl(String pageUrl, int pageId) {
        this.pageUrl = pageUrl;
        this.pageId = pageId;
    }
}