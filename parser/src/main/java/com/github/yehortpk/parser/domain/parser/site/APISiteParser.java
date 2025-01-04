package com.github.yehortpk.parser.domain.parser.site;

import com.github.yehortpk.parser.models.CompanyDTO;
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
public abstract class APISiteParser extends SiteParserImpl {
    private final int DELAY_SEC = 2;
    /**
     * Connection method for the page. GET by default
     * @return connection method
     */
    protected Connection.Method getConnectionMethod() {
        return Connection.Method.GET;
    }

    @Override
    protected PageDTO parsePage(PageConnectionParams pageConnectionParams) throws IOException {
        String pageBody = defaultPageParser.parsePage(pageConnectionParams).getBody();

        Document doc = parsePageBody(pageBody);
        return new PageDTO(pageConnectionParams.getPageUrl(),
                pageConnectionParams.getData(), pageConnectionParams.getHeaders(), doc);
    }

    @Override
    protected PageConnectionParams generatePageConnectionParams(int pageID, CompanyDTO company) {
        String pageUrl = company.getApiVacanciesURL();
        Map<String, String> data = new HashMap<>(company.getData());
        if (data.containsValue("{page}")) {
            data.replaceAll((key, value) -> value.equals("{page}") ? String.valueOf(pageID) : value);
        }

        return PageConnectionParams.builder()
                .data(data)
                .headers(company.getHeaders())
                .connectionMethod(getConnectionMethod())
                .pageUrl(pageUrl)
                .build();
    }

    @Override
    protected int setIntervalBetweenPagesSec(){
        return DELAY_SEC;
    }

    /**
     * Parses page body into Jsoup {@link Document}
     * @param body page body
     * @return Jsoup page document
     */
    protected Document parsePageBody(String body) {
        String finalBody = body.strip().replace("\n", "");

        try {
            Type type = new TypeToken<HashMap<String, Object>>() {}.getType();
            if (finalBody.strip().startsWith("[")) {
                finalBody = "{body:" + body + "}";
            }
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
