package com.github.yehortpk.notifier.services;

import com.github.yehortpk.notifier.models.ProxyDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service("proxyService")
public class ProxyService {
    @Value("${proxy-site-url}")
    private String proxySiteURL;

    private final List<ProxyDTO> proxies = new ArrayList<>();

    public void loadProxies() throws IOException {
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
                proxyDTO.getAnonymity().equals("transparent")
            ) {
                proxies.add(proxyDTO);
            }
        }
    }

    public Proxy getRandomProxy(){
        int randomNum = ThreadLocalRandom.current().nextInt(0, proxies.size() - 1);

        ProxyDTO randomProxy = proxies.get(randomNum);
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(randomProxy.getProxyHost(), randomProxy.getProxyPort()));
    }
}
