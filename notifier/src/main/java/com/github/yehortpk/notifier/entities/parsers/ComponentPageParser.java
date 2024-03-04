package com.github.yehortpk.notifier.entities.parsers;

import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Setter
public class ComponentPageParser extends PageParserImpl {
    private String dynamicElementQuerySelector;
    private String chromeDriverPath;
    private String chromePath;
    private volatile boolean streamIsClosed = false;

    public ComponentPageParser(String pageUrl, int pageId) {
        super(pageUrl, pageId);
    }

    @Override
    public Document parsePage(Proxy proxy) {String[] proxyData = retrieveDataFromProxy(proxy);
        ChromeOptions chromeOptions = getChromeOptions();
        chromeOptions.addArguments(String.format("--proxy-server=%s:%s", proxyData[0], proxyData[1]));
        Document document = parsePage(chromeOptions, proxy);
        System.out.printf("Page %s parsed with proxy %s %n", pageUrl, proxy);
        return document;
    }

    @Override
    public Document parsePage() {
        Document document = parsePage(getChromeOptions(), null);
        System.out.printf("Page %s parsed without proxy %n", pageUrl);
        return document;
    }

    protected Document parsePage (ChromeOptions chromeOptions, @Nullable Proxy proxy) {
        // Check if proxy capable of load page, then with webdriver (memory efficiently)
        try {
            Jsoup.connect(pageUrl)
                    .proxy(proxy)
                    .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316" +
                            " Firefox/3.6.2")
                    .headers(headers)
                    .data(data)
                    .method(parseMethod)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Some bug with unclosed stream in selenium https://github.com/SeleniumHQ/selenium/issues/13096
        if (!streamIsClosed) {

            synchronized (ComponentPageParser.class) {
                WebDriver driver = getWebDriver(chromeOptions);

                String finalPageUrl = pageUrl;
                if (!data.isEmpty()) {
                    finalPageUrl += "?" + data.entrySet().stream()
                            .map(entry -> entry.getKey() + "=" + entry.getValue())
                            .collect(Collectors.joining("&"));
                }

                driver.get(finalPageUrl);

                Wait<WebDriver> wait = new FluentWait<>(driver)
                        .withTimeout(Duration.of(20, ChronoUnit.SECONDS))
                        .pollingEvery(Duration.of(5, ChronoUnit.SECONDS))
                        .ignoring(NoSuchElementException.class)
                        .ignoring(IOException.class);
                wait.until(dr -> dr.findElement(By.cssSelector(dynamicElementQuerySelector)));

                final String htmlContent = driver.getPageSource();
                streamIsClosed = true;
                driver.quit();

                return Jsoup.parse(htmlContent);
            }
        }

        return null;
    }

    private String[] retrieveDataFromProxy(Proxy proxy) {
        InetSocketAddress address = (InetSocketAddress) proxy.address();

        return new String[]{address.getHostString(), String.valueOf(address.getPort())};
    }

    private WebDriver getWebDriver(ChromeOptions chromeOptions) {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        return new ChromeDriver(chromeOptions);
    }

    private ChromeOptions getChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless", "--disable-dev-shm-usage", "--no-sandbox");
        chromeOptions.setBinary(chromePath);

        return chromeOptions;
    }
}
