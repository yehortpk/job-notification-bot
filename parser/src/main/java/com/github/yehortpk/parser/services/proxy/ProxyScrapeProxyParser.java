package com.github.yehortpk.parser.services.proxy;

import com.google.gson.*;

import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ProxyScrapeProxyParser implements ProxyParser{
    @Override
    public List<Proxy> parseProxies() {
        List<Proxy> proxies = new ArrayList<>();
        final String FREE_PROXIES_SITE_URL = "https://api.proxyscrape.com/v4/free-proxy-list/get" +
                "?request=display_proxies&proxy_format=ipport&format=json&timeout=20000";
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(FREE_PROXIES_SITE_URL).toURL().openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(60 * 1000);
            connection.setReadTimeout(60 * 1000);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                    JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
                    List<JsonElement> proxiesArray = ((JsonArray) jsonResponse.get("proxies")).asList();
                    for (JsonElement proxy : proxiesArray) {
                        ProxyDTO proxyDTO = ProxyDTO.builder()
                                .proxyHost(proxy.getAsJsonObject().get("ip").getAsString())
                                .proxyPort(proxy.getAsJsonObject().get("port").getAsInt())
                                .anonymity(proxy.getAsJsonObject().get("anonymity").getAsString())
                                .isHTTPS(proxy.getAsJsonObject().get("ssl").getAsBoolean())
                                .build();

                        if(proxyDTO.getAnonymity().equals("transparent")) {
                            Proxy newProxy =
                                    new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyDTO.getProxyHost(), proxyDTO.getProxyPort()));
                            proxies.add(newProxy);
                        }
                    }
                }
            } else {
                throw new RuntimeException("Failed to fetch proxies: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch proxies: " + e.getMessage(), e);
        }

        return proxies;
    }
}
