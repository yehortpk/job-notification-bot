package com.github.yehortpk.parser.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Connection;

import java.io.Serializable;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * DTO representing connection params of the page
 */
@Getter
@Setter
@Builder
public class PageConnectionParams implements Serializable {
    private String pageUrl;
    @Builder.Default
    private Map<String, String> data = new HashMap<>();
    @Builder.Default
    private Map<String, String> headers = new HashMap<>();
    @Builder.Default
    private Connection.Method connectionMethod = Connection.Method.GET;
    private Proxy proxy;
}
