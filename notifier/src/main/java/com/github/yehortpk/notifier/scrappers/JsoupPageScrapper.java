package com.github.yehortpk.notifier.scrappers;

import com.github.yehortpk.notifier.exceptions.PageConnectionException;
import com.github.yehortpk.notifier.models.PageConnectionParams;
import com.github.yehortpk.notifier.services.ProxyService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Proxy;

@Setter
@Slf4j
public class JsoupPageScrapper extends PageScrapperImpl{
    protected final PageConnectionParams pageConnectionParams;

    public JsoupPageScrapper(PageConnectionParams pageConnectionParams, ProxyService proxyService) {
        super(pageConnectionParams, proxyService);
        this.pageConnectionParams = pageConnectionParams;
    }

    @Override
    protected String scrapPage(Proxy proxy) {
        try {
            String pageHTML = pageConnectionParams.toConnection().proxy(proxy).execute().body();
            pageConnectionParams.setProxy(proxy);
            return pageHTML;
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw new PageConnectionException(e);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String scrapPage() {
        try {
            return pageConnectionParams.toConnection().execute().body();
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw new PageConnectionException(e);
            }
            throw new RuntimeException(e);
        }
    }
}
