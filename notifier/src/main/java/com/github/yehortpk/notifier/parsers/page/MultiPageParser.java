package com.github.yehortpk.notifier.parsers.page;

import com.github.yehortpk.notifier.models.PageConnectionParams;
import com.github.yehortpk.notifier.scrappers.JsoupPageScrapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Setter
@Slf4j
@RequiredArgsConstructor
public class MultiPageParser implements PageParser {
    private final JsoupPageScrapper pageScrapper;

    @Override
    public Document parsePage() {
        Document page = Jsoup.parse(pageScrapper.loadPage());
        PageConnectionParams pageConnectionParams = pageScrapper.getPageConnectionParams();
        log.info("Page: {} parsed with proxy: {}",
                pageConnectionParams.getPageUrl(), pageConnectionParams.getProxy());
        return page;
    }
}
