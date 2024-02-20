package com.github.yehortpk.notifier.entities.parsers;

import org.jsoup.nodes.Document;

import java.net.Proxy;
import java.util.concurrent.Callable;

public interface PageParser {
    Callable<Document> parsePage(Proxy proxy);
    Document parsePage();
}
