package com.github.yehortpk.notifier.entities.parsers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
public class SinglePageParser extends PageParserImpl {
    public SinglePageParser(String pageUrl, int pageId) {
        super(pageUrl, pageId);
    }

    @Override
    public Document parsePage(Proxy proxy) {
        pageUrl = pageUrl.formatted(pageId);
        Connection con = HttpConnection.connect(pageUrl);
        Connection.Response response;
        try {
            response = con.method(parseMethod)
                    .data(data)
                    .headers(headers)
                    .ignoreContentType(true)
                    .proxy(proxy)
                    .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316" +
                            " Firefox/3.6.2")
                    .ignoreContentType(true)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        synchronized (SinglePageParser.class) {
            Document document = parseBody(response);
            System.out.printf("Page parsed: %s page_id:%s, proxy:%s%n", pageUrl, pageId, proxy);
            return document;
        }
    }

    @Override
    public Document parsePage() {
        Connection con = HttpConnection.connect(pageUrl);
        Connection.Response response;
        try {
            response = con.method(parseMethod)
                    .data(data)
                    .headers(this.headers)
                    .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316" +
                            " Firefox/3.6.2")
                    .ignoreContentType(true)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        synchronized (SinglePageParser.class) {
            Document document = parseBody(response);

            System.out.printf("Page parsed: %s page_id:%s without proxy%n", pageUrl, pageId);
            return document;
        }
    }

    private Element createElementFromJson(Element element, Object obj) {
        if (obj instanceof Map) {
            Map<String, Object> nestedMap = (Map<String, Object>) obj;
            for (Map.Entry<String, Object> entry : nestedMap.entrySet()) {
                String key = entry.getKey();
                Object nestedValue = entry.getValue();

                Element childElement = new Element("div");
                childElement.attr("data-key", key);

                element.appendChild(createElementFromJson(childElement, nestedValue));
            }
        } else if (obj instanceof List<?> list) {
            for (Object listItem : list) {
                Element childElement = new Element("div");
                element.appendChild(createElementFromJson(childElement, listItem));
            }
        } else {
            if(obj == null) {
                obj = "null";
            }

            element.html("<div>%s</div>".formatted(obj.toString()
                    .strip()
                    .replace("\n", "")
            ));
        }

        return element;
    }

    private Document parseBody(Connection.Response response) {
        String body = response.body().strip().replace("\n", "");

        Type type = new TypeToken<HashMap<String, Object>>() {}.getType();
        HashMap<String, Object> hashMap = new Gson().fromJson(body, type);
        Document document = new Document("");
        Element bodyElement = document.appendElement("body");
        for (Map.Entry<String, Object> jsonElement : hashMap.entrySet()) {
            String key = jsonElement.getKey();
            Object obj = jsonElement.getValue();
            Element element = new Element("div");
            element.attr("data-key", key);
            bodyElement.appendChild(createElementFromJson(element, obj));
        }

        return document;
    }
}
