package com.github.yehortpk.parser.models;

import com.github.yehortpk.parser.domain.connectors.DelayedPageConnector;
import com.github.yehortpk.parser.domain.scrappers.ComponentPageScrapper;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Connection;

import java.net.Proxy;
import java.util.Map;

/**
 * DTO representing connection params of the page
 */
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
    /**
     * Delay before the connection (ms). Used in {@link DelayedPageConnector}
     */
    @Builder.Default
    private int delay = 0;

    /**
     * Element selector that page looking for when parsing page. Used in {@link ComponentPageScrapper}
     */
    @Builder.Default
    private String dynamicElementQuerySelector = "body";
}
