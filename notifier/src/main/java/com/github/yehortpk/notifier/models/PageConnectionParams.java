package com.github.yehortpk.notifier.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Connection;

import java.net.Proxy;
import java.util.Map;

@Getter
@Setter
@Builder
public class PageConnectionParams {
    private String pageUrl;
    private Map<String, String> data;
    private Map<String, String> headers;
    @Builder.Default
    private Connection.Method connectionMethod = Connection.Method.GET;
    @Builder.Default
    private String userAgent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2)" +
            " Gecko/20100316 Firefox/3.6.2";
    private Proxy proxy;
    @Builder.Default
    private int delay = 0;
    @Builder.Default
    private String dynamicElementQuerySelector = "body";
}
