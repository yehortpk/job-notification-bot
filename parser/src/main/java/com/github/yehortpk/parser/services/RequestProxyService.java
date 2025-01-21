package com.github.yehortpk.parser.services;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

public class RequestProxyService {
    private static final ThreadLocal<BrowserMobProxy> proxyThreadLocal = new ThreadLocal<>();

    public RequestProxyService (Proxy chainedProxy) {
        String[] chainedProxyParams = retrieveDataFromProxy(chainedProxy);
        BrowserMobProxy proxy = proxyThreadLocal.get();

        if (proxy == null) {
            proxy = createProxy(chainedProxyParams[0], Integer.parseInt(chainedProxyParams[1]));
            proxyThreadLocal.set(proxy);
        }
    }

    public RequestProxyService () {
        BrowserMobProxy proxy = proxyThreadLocal.get();

        if (proxy == null) {
            proxy = createProxy();
            proxyThreadLocal.set(proxy);
        }
    }

    public BrowserMobProxy getProxy() {
        return proxyThreadLocal.get();
    }

    private BrowserMobProxy createProxy() {
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxyThreadLocal.set(proxy);

        return proxy;
    }

    private BrowserMobProxy createProxy(String chainedProxyHost, int chainedProxyPort) {
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.setChainedProxy(new InetSocketAddress(chainedProxyHost, chainedProxyPort));

        return proxy;
    }

    public synchronized void addHeaders(Map<String, String> headers) {
        if (!headers.isEmpty()) {
            proxyThreadLocal.get().addHeaders(headers);
        }
    }

    public void startService() {
        proxyThreadLocal.get().start(0);
    }

    public void stopService() {
        BrowserMobProxy proxy = proxyThreadLocal.get();
        if (proxy != null) {
            try {
                proxy.stop();
            } finally {
                proxyThreadLocal.remove();
            }
        }
    }

    /**
     * Retrieves data (host, port) from {@link Proxy} object
     * @param proxy proxy object
     * @return proxy data array with size of 2 (host and port)
     */
    private String[] retrieveDataFromProxy(Proxy proxy) {
        InetSocketAddress address = (InetSocketAddress) proxy.address();

        return new String[]{address.getHostString(), String.valueOf(address.getPort())};
    }
}
