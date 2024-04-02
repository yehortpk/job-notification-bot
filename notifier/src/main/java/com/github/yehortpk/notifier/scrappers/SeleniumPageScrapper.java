package com.github.yehortpk.notifier.scrappers;

import com.github.yehortpk.notifier.models.PageConnectionParams;
import com.github.yehortpk.notifier.parsers.page.ComponentPageParser;
import com.github.yehortpk.notifier.services.ProxyService;
import lombok.Cleanup;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

public class SeleniumPageScrapper extends PageScrapperImpl {
    protected final PageConnectionParams pageConnectionParams;
    private final String dynamicElementQuerySelector;
    private final String chromeDriverPath;
    private final String chromePath;

    public SeleniumPageScrapper(PageConnectionParams pageConnectionParams, ProxyService proxyService, String dynamicElementQuerySelector, String chromeDriverPath, String chromePath) {
        super(pageConnectionParams, proxyService);
        this.pageConnectionParams = pageConnectionParams;
        this.dynamicElementQuerySelector = dynamicElementQuerySelector;
        this.chromeDriverPath = chromeDriverPath;
        this.chromePath = chromePath;
    }

    @Override
    protected String scrapPage(Proxy proxy) {
        ChromeOptions chromeOptions = createChromeOptions(proxy);

        JsoupPageScrapper jsoupPageScrapper = new JsoupPageScrapper(pageConnectionParams, proxyService);
        // Check proxy. Jsoup scrapper is more lightweight. If proxy is working - parsing with selenium
        jsoupPageScrapper.scrapPage(proxy);
        String pageHTML = scrapPage(chromeOptions);
        pageConnectionParams.setProxy(proxy);
        return pageHTML;
    }

    @Override
    protected String scrapPage() {
        ChromeOptions chromeOptions = createChromeOptions();
        return scrapPage(chromeOptions);
    }

    private String scrapPage(ChromeOptions chromeOptions) {


        synchronized (ComponentPageParser.class) {
            @Cleanup WebDriver driver = getWebDriver(chromeOptions);

            String finalPageUrl = constructURLWithData();

            driver.get(finalPageUrl);

            Wait<WebDriver> wait = new FluentWait<>(driver)
                    .withTimeout(Duration.of(20, ChronoUnit.SECONDS))
                    .pollingEvery(Duration.of(5, ChronoUnit.SECONDS))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(IOException.class);
            wait.until(dr -> dr.findElement(By.cssSelector(dynamicElementQuerySelector)));

            return driver.getPageSource();
        }
    }

    private String[] retrieveDataFromProxy(Proxy proxy) {
        InetSocketAddress address = (InetSocketAddress) proxy.address();

        return new String[]{address.getHostString(), String.valueOf(address.getPort())};
    }

    private WebDriver getWebDriver(ChromeOptions chromeOptions) {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        return new ChromeDriver(chromeOptions);
    }

    private String constructURLWithData() {
        Map<String, String> data = pageConnectionParams.getData();
        String pageUrl = pageConnectionParams.getPageUrl();
        if (!data.isEmpty()) {
            return pageUrl + "?" + data.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));
        }

        return pageUrl;

    }

    private ChromeOptions createChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless", "--disable-dev-shm-usage", "--no-sandbox");
        chromeOptions.setBinary(chromePath);

        return chromeOptions;
    }

    private ChromeOptions createChromeOptions(Proxy proxy) {
        ChromeOptions chromeOptions = createChromeOptions();
        String[] proxyData = retrieveDataFromProxy(proxy);
        chromeOptions.addArguments(String.format("--proxy-server=%s:%s", proxyData[0], proxyData[1]));

        return  chromeOptions;
    }
}
