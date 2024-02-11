package com.github.yehortpk.notifier.services;

import com.github.yehortpk.notifier.models.ProxyDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class ProxyService {
    private static ProxyService instance;

    public static ProxyService getInstance() {
        if (instance == null) {
            instance = new ProxyService();
        }
        return instance;
    }

    private final List<ProxyDTO> proxies = new ArrayList<>();
    private static final ThreadLocal<Integer> threadLocalParam = ThreadLocal.withInitial(() -> 0);

    public void loadProxies() throws IOException {
        String proxySiteURL = "https://free-proxy-list.net/";
        Document page = Jsoup.connect(proxySiteURL).get();
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

            // Filtering by non-https ip and anonymity
            if(
                !proxyDTO.getCountryCode().isEmpty() &&
                        proxyDTO.getAnonymity().equals("elite proxy")
            ) {
                proxies.add(proxyDTO);
            }
        }
    }

    public Proxy getRandomProxy(){
        int randomNum = (threadLocalParam.get() + proxies.size()) % proxies.size();

        ProxyDTO randomProxy = proxies.get(randomNum);
        threadLocalParam.set(++randomNum);
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(randomProxy.getProxyHost(), randomProxy.getProxyPort()));
    }
}
