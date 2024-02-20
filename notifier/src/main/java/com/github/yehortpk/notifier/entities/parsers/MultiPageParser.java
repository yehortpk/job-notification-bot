package com.github.yehortpk.notifier.entities.parsers;

import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.Proxy;
import java.util.concurrent.Callable;

@Setter
public class MultiPageParser extends PageParserImpl {
    public MultiPageParser(String pageUrl, int pageId) {
        super(pageUrl, pageId);
    }

    @Override
    public Callable<Document> parsePage(Proxy proxy) {
        String finalPageUrl = pageUrl.formatted(pageId);
        return () -> {
            Document document = Jsoup.connect(finalPageUrl)
                    .proxy(proxy)
                    .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316" +
                            " Firefox/3.6.2")
                    .headers(this.headers)
                    .method(parseMethod)
                    .execute()
                    .parse();
            System.out.printf("Page %s parsed with proxy %s%n", finalPageUrl, proxy);
            return document;
        };
    }

    @Override
    public Document parsePage() {
        try {
            return Jsoup.connect(pageUrl)
                    .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316" +
                            " Firefox/3.6.2")
                    .headers(this.headers)
                    .method(parseMethod)
                    .execute()
                    .parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
