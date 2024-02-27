package com.github.yehortpk.notifier.entities.parsers;

import org.jsoup.nodes.Document;

import java.net.Proxy;

public interface PageParser {
    Document parsePage(Proxy proxy);
    Document parsePage();
}
