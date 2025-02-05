package com.github.yehortpk.parser.scrapper;

import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.services.PlaywrightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

/**
 * Component-based site page scrapper. Uses dynamicElementQuerySelector from {@link PageConnectionParams}
 * to delay page scrapping until the element on the page is loaded. Works on Selenium
 */
@Slf4j
@RequiredArgsConstructor
public class ComponentPageScrapper implements PageScrapper {
    private final String dynamicElementQuerySelector;
    private final PlaywrightService playwrightService = new PlaywrightService();

    @Override
    public PageScrapperResponse scrapPage(PageConnectionParams pageConnectionParams) throws IOException {
        Map<String, String> headers = pageConnectionParams.getHeaders();
        String proxy = pageConnectionParams.getProxy() == null? null: convertProxyToString(pageConnectionParams.getProxy());
        String pageURL = constructURLWithData(pageConnectionParams.getPageUrl(), pageConnectionParams.getData());

        return playwrightService.scrapPage(pageURL, dynamicElementQuerySelector, pageConnectionParams.getTimeoutSec(), headers, proxy);
    }

    /**
     * Construct GET url with the data map as query params
     * @param pageUrl base url without query params
     * @param data data for query params
     * @return final url
     */
    private String constructURLWithData(String pageUrl, Map<String, String> data) {
        if (!data.isEmpty()) {
            StringBuilder pageUrlBuilder = new StringBuilder(pageUrl + "?");
            for (Map.Entry<String, String> dataES : data.entrySet()) {
                if (dataES.getKey().endsWith("[]")) {
                    for (String valuePart : dataES.getValue().split(",")) {
                        pageUrlBuilder.append(dataES.getKey()).append("=").append(valuePart).append("&");
                    }
                } else {
                    pageUrlBuilder.append(dataES.getKey()).append("=").append(dataES.getValue()).append("&");
                }

            }
            pageUrl = pageUrlBuilder.toString();
        }

        return pageUrl;
    }

    private String convertProxyToString(Proxy proxy) {
        InetSocketAddress addr = (InetSocketAddress) proxy.address();
        String protocol = proxy.type() == Proxy.Type.SOCKS ? "socks5" : "http";
        return protocol + "://" + addr.getHostString() + ":" + addr.getPort();
    }
}
