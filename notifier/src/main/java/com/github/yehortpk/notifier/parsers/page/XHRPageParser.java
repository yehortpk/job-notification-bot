package com.github.yehortpk.notifier.parsers.page;

import com.github.yehortpk.notifier.models.PageConnectionParams;
import com.github.yehortpk.notifier.scrappers.JsoupPageScrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Slf4j
@RequiredArgsConstructor
public class XHRPageParser implements PageParser {
    private final JsoupPageScrapper pageScrapper;

    @Override
    public Document parsePage() {
        Document page = parseBody(pageScrapper.loadPage());
        PageConnectionParams pageConnectionParams = pageScrapper.getPageConnectionParams();
        log.info("Page: {} parsed with proxy: {}",
                pageConnectionParams.getPageUrl(), pageConnectionParams.getProxy());
        return page;
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

    private Document parseBody(String body) {
        String finalBody = body.strip().replace("\n", "");

        Type type = new TypeToken<HashMap<String, Object>>() {}.getType();
        HashMap<String, Object> hashMap = new Gson().fromJson(finalBody, type);
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
