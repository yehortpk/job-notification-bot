package com.github.yehortpk.notifier.services;

import com.github.yehortpk.notifier.models.ProxyDTO;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Getter
public class ProxyService {
    @Value("${proxy-site-url}")
    private String proxySiteURL;

    private final List<ProxyDTO> proxies = new ArrayList<>();
    private static final ThreadLocal<List<Integer>> threadLocalParam = ThreadLocal.withInitial(ArrayList::new);

    public void loadProxies() throws IOException {
        Document page = Jsoup.connect(proxySiteURL)
                .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316" +
                        " Firefox/3.6.2")
                .header("Content-Language", "en-US")
                .timeout(60 * 1000)
                .get();
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
                !proxyDTO.getCountryCode().isEmpty()
//                        &&  proxyDTO.getAnonymity().equals("elite proxy")
            ) {
                proxies.add(proxyDTO);
            }
        }
        resetRange();
    }

    private List<Integer> resetRange() {
        List<Integer> rangeList = IntStream.range(0, proxies.size())
                .boxed()
                .collect(Collectors.toList());
        threadLocalParam.set(rangeList);
        return threadLocalParam.get();
    }

    public Proxy getRandomProxy(){
        List<Integer> range = threadLocalParam.get();
        if(range.isEmpty()) {
            range = resetRange();
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(0, range.size());
        int randomNum = range.remove(randomIndex);
        threadLocalParam.set(range);

        ProxyDTO randomProxy = proxies.get(randomNum);

        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(randomProxy.getProxyHost(), randomProxy.getProxyPort()));
    }
}
