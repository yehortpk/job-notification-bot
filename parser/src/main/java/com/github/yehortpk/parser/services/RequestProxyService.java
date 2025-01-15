package com.github.yehortpk.parser.services;

import jakarta.annotation.PreDestroy;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.Map;

@Service
public class RequestProxyService {
    private final BrowserMobProxy browserMobProxy;

    public RequestProxyService() {
        browserMobProxy = new BrowserMobProxyServer();
        browserMobProxy.setTrustAllServers(true);
        startProxy();
    }

    public void startProxy() {
        browserMobProxy.start();
    }

    public synchronized BrowserMobProxy createSeleniumProxy(Map<String, String> headers) {
        browserMobProxy.addHeaders(headers);

        return browserMobProxy;
    }

    public synchronized BrowserMobProxy createSeleniumProxy(Map<String, String> headers, String[] proxyServer) {
        browserMobProxy.addHeaders(headers);
        browserMobProxy.setChainedProxy(new InetSocketAddress(proxyServer[0], Integer.parseInt(proxyServer[1])));

        return browserMobProxy;
    }

    @PreDestroy
    public void stopService() {
        browserMobProxy.stop();
    }
}
