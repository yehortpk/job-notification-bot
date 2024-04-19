package com.github.yehortpk.parser.domain.parsers.site;

import com.github.yehortpk.parser.domain.connectors.PageConnector;
import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.models.PageDTO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Site parser based on XHR requests for the pages
 */
@Component
@ToString(callSuper = true)
@Getter
public abstract class XHRSiteParser extends SiteParserImpl {
    @Autowired
    private PageConnector defaultPageConnector;

    private final int DELAY_SEC = 1;

    /**
     * Connection method for the page. GET by default
     * @return connection method
     */
    protected Connection.Method getConnectionMethod() {
        return Connection.Method.GET;
    }

    @Override
    public PageDTO parsePage(int pageId) throws IOException {
        String pageUrl = company.getSinglePageRequestLink();
        PageConnectionParams pageConnectionParams = PageConnectionParams.builder()
                .data(createData(pageId))
                .headers(createHeaders())
                .connectionMethod(getConnectionMethod())
                .pageUrl(pageUrl)
                .delay(pageId * DELAY_SEC * 1000)
                .build();

        String pageBody = defaultPageConnector.connectToPage(pageConnectionParams);

        Document doc = parsePageBody(pageBody);
        return new PageDTO(pageUrl, pageId, doc);
    }

    /**
     * Parses page body into Jsoup {@link Document}
     * @param body page body
     * @return Jsoup page document
     */
    private Document parsePageBody(String body) {
        String finalBody = body.strip().replace("\n", "");

        try {
            Type type = new TypeToken<HashMap<String, Object>>() {}.getType();
            HashMap<String, Object> hashMap = new Gson().fromJson(finalBody, type);
            Document document = new Document("");
            Element bodyElement = document.appendElement("body");

            for (Map.Entry<String, Object> jsonElement : hashMap.entrySet()) {
                String key = jsonElement.getKey();
                Object value = jsonElement.getValue();
                Element element = new Element("div");
                element.attr("data-key", key);
                bodyElement.appendChild(createElementFromJson(element, value));
            }

            return document;
        } catch (JsonSyntaxException ignored) {
            return Jsoup.parse(finalBody);
        }

    }

    /**
     * Creates Jsoup DOM {@link Element} from recursively traversing JSON
     * It creates div tag with [data-key=key] attribute for every key in the JSON and recursively iterate through
     * all his inner objects
     * @param element current element for traverse
     * @param value object value
     * @return Jsoup DOM {@link Element}
     */
    private Element createElementFromJson(Element element, Object value) {
        if (value instanceof Map) {
            Map<String, Object> nestedMap = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : nestedMap.entrySet()) {
                String key = entry.getKey();
                Object nestedValue = entry.getValue();

                Element childElement = new Element("div");
                childElement.attr("data-key", key);

                element.appendChild(createElementFromJson(childElement, nestedValue));
            }
        } else if (value instanceof List<?> list) {
            for (Object listItem : list) {
                Element childElement = new Element("div");
                element.appendChild(createElementFromJson(childElement, listItem));
            }
        } else {
            if(value == null) {
                value = "null";
            }

            element.html("<div>%s</div>".formatted(value.toString()
                    .strip()
                    .replace("\n", "")
            ));
        }

        return element;
    }
}
