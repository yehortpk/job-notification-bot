package com.github.yehortpk.parser.services.proxy;

import com.github.yehortpk.parser.models.ProxyDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class FreeProxyListProxyParser implements ProxyParser{
    @Override
    public List<Proxy> parseProxies() {
        List<Proxy> proxies = new ArrayList<>();
        final String FREE_PROXIES_SITE_URL = "https://free-proxy-list.net/";
        Document page;
        try {
            page = Jsoup.connect(FREE_PROXIES_SITE_URL)
                    .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316" +
                            " Firefox/3.6.2")
                    .header("Content-Language", "en-US")
                    .timeout(60 * 1000)
                    .get();
        } catch (IOException e) {
            throw new RuntimeException("Cant load proxies: " + e.getMessage());
        }
        List<Element> rows = page.select(".fpl-list tbody > tr");
        for (Element row : rows) {
            List<Element> columns = row.select("td");
            ProxyDTO proxyDTO = ProxyDTO.builder()
                    .proxyHost(columns.get(0).text())
                    .proxyPort(Integer.parseInt(columns.get(1).text()))
                    .countryCode(columns.get(2).text())
                    .countryTitle(columns.get(3).text())
                    .anonymity(columns.get(4).text())
                    .isGoogle(columns.get(5).text().equals("yes"))
                    .isHTTPS(columns.get(6).text().equals("yes"))
                    .lastChecked(columns.get(7).text())
                    .build();

            if(!proxyDTO.getCountryCode().isEmpty() &&  !proxyDTO.getAnonymity().equals("transparent")) {
                Proxy newProxy =
                        new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyDTO.getProxyHost(), proxyDTO.getProxyPort()));
                proxies.add(newProxy);
            }
        }

        return proxies;
    }
}
