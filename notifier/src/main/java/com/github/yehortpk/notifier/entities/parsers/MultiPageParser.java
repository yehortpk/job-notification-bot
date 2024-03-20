package com.github.yehortpk.notifier.entities.parsers;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.Proxy;

@Setter
@Slf4j
public class MultiPageParser extends PageParserImpl {
    public MultiPageParser(String pageUrl, int pageId) {
        super(pageUrl, pageId);
    }

    @Override
    public Document parsePage(Proxy proxy) {
        String finalPageUrl = pageUrl.formatted(pageId);
        Document document;
        try {
            document = Jsoup.connect(finalPageUrl)
                    .proxy(proxy)
                    .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316" +
                            " Firefox/3.6.2")
                    .headers(headers)
                    .data(data)
                    .method(parseMethod)
                    .execute()
                    .parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.debug("Page {} parsed with proxy {}", finalPageUrl, proxy);
        return document;
    }

    @Override
    public Document parsePage() {
        try {
            String finalPageUrl = pageUrl.formatted(pageId);
            Document document = Jsoup.connect(finalPageUrl)
                    .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316" +
                            " Firefox/3.6.2")
                    .headers(headers)
                    .data(data)
                    .method(parseMethod)
                    .execute()
                    .parse();

            log.debug("Page {} parsed without proxy", finalPageUrl);
            return document;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
