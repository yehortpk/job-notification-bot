package com.github.yehortpk.notifier.entities.parsers;

import org.jsoup.nodes.Document;

public interface PageParser {
    Document loadPage(String pageURL);
}
