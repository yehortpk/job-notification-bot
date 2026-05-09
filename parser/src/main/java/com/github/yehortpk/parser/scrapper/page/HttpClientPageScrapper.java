package com.github.yehortpk.parser.scrapper.page;

import com.github.yehortpk.parser.models.PageRequestParams;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * java.net.httpHTTPRequest based pages scrapper. More low-level than {@link JsoupPageScrapper}.
 */
@Slf4j
public class HttpClientPageScrapper implements PageScrapper {
    @Override
    public PageScrapperResponse scrapPage(PageRequestParams pageRequestParams) throws IOException {
        // Build request based on HTTP method and payload configuration
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        if (pageRequestParams.getConnectionMethod() == Connection.Method.GET) {
            requestBuilder = requestBuilder
                    .GET()
                    .uri(
                        URI.create(constructURLWithData(pageRequestParams.getPageURL(), pageRequestParams.getData()))
                    );
        } else {
            if (pageRequestParams.getRequestBody() != null) {
                requestBuilder = requestBuilder
                        .POST(HttpRequest.BodyPublishers.ofString(pageRequestParams.getRequestBody()));
            } else {
                String formBody = pageRequestParams.getData().entrySet().stream()
                        .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                                + "="
                                + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                        .collect(Collectors.joining("&"));
                requestBuilder = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(formBody));
            }
            requestBuilder = requestBuilder
                    .uri(
                        URI.create(pageRequestParams.getPageURL())
                    );
        }

        // Apply headers and cookies to the request
        pageRequestParams.getHeaders().forEach(requestBuilder::header);
        Optional<String> cookies = convertMapParamsToString(pageRequestParams.getCookies());
        if (cookies.isPresent()) {
            requestBuilder = requestBuilder.header("Cookie", cookies.get());
        }

        HttpClient.Builder clientBuilder = HttpClient.newBuilder();
        clientBuilder = clientBuilder.connectTimeout(Duration.ofSeconds(30));

        // Manage cookies for this request/response cycle
        CookieManager cookieManager = new CookieManager();
        CookieStore cookieStore = cookieManager.getCookieStore();
        clientBuilder = clientBuilder.cookieHandler(cookieManager);

        // Manage proxies settings
        Proxy proxy = pageRequestParams.getProxy();
        if (proxy != null) {
            clientBuilder = clientBuilder.proxy(ProxySelector.of((InetSocketAddress) proxy.address()));
        }

        try (HttpClient client = clientBuilder.version(HttpClient.Version.HTTP_1_1).build()) {
            HttpRequest request = requestBuilder.build();

            log.info("Connecting to the page {}, method: {}, proxy: {},  data: {}, headers: {}, cookies: {}",
                    pageRequestParams.getPageURL(),
                    pageRequestParams.getConnectionMethod(),
                    pageRequestParams.getProxy(),
                    pageRequestParams.getData(),
                    pageRequestParams.getHeaders(),
                    pageRequestParams.getCookies()
            );

            java.net.http.HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            log.info("Connection to the page {}, method: {}, proxy: {},  data: {} was established",
                    pageRequestParams.getPageURL(),
                    pageRequestParams.getConnectionMethod(),
                    pageRequestParams.getProxy(),
                    pageRequestParams.getData()
            );

            // Extract response headers and cookies
            Map<String, String> responseHeaders = new HashMap<>();
            Map<String, String> responseCookies = new HashMap<>();

            for (Map.Entry<String, List<String>> es : response.headers().map().entrySet()) {
                responseHeaders.put(es.getKey(), es.getValue().getFirst());
            }

            for (HttpCookie cookie : cookieStore.getCookies()) {
                responseCookies.put(cookie.getName(), cookie.getValue());
            }

            return new PageScrapperResponse(responseHeaders, responseCookies, response.body());
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    private Optional<String> convertMapParamsToString(Map<String, String> paramsMap) {
        if(paramsMap.isEmpty()) {
            return Optional.empty();
        }

        StringBuilder paramsStringBuilder = new StringBuilder();
        paramsMap.forEach((key, value) -> paramsStringBuilder.append(key).append("=").append(value).append("; "));
        String result = paramsStringBuilder.toString();
        result = result.substring(0, result.length() - 2);

        return Optional.of(result);
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
}
