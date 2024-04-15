package com.github.yehortpk.parser.domain.scrappers;

import com.github.yehortpk.parser.models.PageConnectionParams;
import lombok.Cleanup;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.ErrorHandler;
import org.openqa.selenium.remote.http.ConnectionFailedException;
import org.openqa.selenium.safari.ConnectionClosedException;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ComponentPageScrapper implements PageScrapper {
    @Override
    public String scrapPage(PageConnectionParams pageConnectionParams) {
        ChromeOptions chromeOptions;
        if (pageConnectionParams.getProxy() != null) {
            chromeOptions = createChromeOptions(pageConnectionParams.getProxy());
        } else {
            chromeOptions = createChromeOptions();
        }

        @Cleanup WebDriver driver = getWebDriver(chromeOptions);
        String finalPageUrl = constructURLWithData(pageConnectionParams.getPageUrl(), pageConnectionParams.getData());

        return scrapPage(finalPageUrl, driver, pageConnectionParams.getDynamicElementQuerySelector());
    }

    private String scrapPage(String pageUrl, WebDriver driver, String dynamicElementQuerySelector) {
        synchronized (ComponentPageScrapper.class) {

            driver.get(pageUrl);

            Wait<WebDriver> wait = new FluentWait<>(driver)
                    .withTimeout(Duration.of(20, ChronoUnit.SECONDS))
                    .pollingEvery(Duration.of(5, ChronoUnit.SECONDS))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(ConnectionClosedException.class)
                    .ignoring(ConnectionFailedException.class)
                    .ignoring(TimeoutException.class)
                    .ignoring(ErrorHandler.UnknownServerException.class);

            wait.until(dr -> dr.findElement(By.cssSelector(dynamicElementQuerySelector)));

            return driver.getPageSource();
        }
    }

    private String[] retrieveDataFromProxy(Proxy proxy) {
        InetSocketAddress address = (InetSocketAddress) proxy.address();

        return new String[]{address.getHostString(), String.valueOf(address.getPort())};
    }

    @Value("${webdriver.chrome.driver}")
    private String chromeDriverPath;

    private WebDriver getWebDriver(ChromeOptions chromeOptions) {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        return new ChromeDriver(chromeOptions);
    }

    private String constructURLWithData(String pageUrl, Map<String, String> data) {
        if (!data.isEmpty()) {
            return pageUrl + "?" + data.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));
        }

        return pageUrl;

    }

    @Value("${webdriver.chrome.path}")
    private String chromeBinaryPath;

    private ChromeOptions createChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless", "--disable-dev-shm-usage", "--no-sandbox");

        chromeOptions.setBinary(chromeBinaryPath);

        return chromeOptions;
    }

    private ChromeOptions createChromeOptions(Proxy proxy) {
        ChromeOptions chromeOptions = createChromeOptions();
        String[] proxyData = retrieveDataFromProxy(proxy);
        chromeOptions.addArguments(String.format("--proxy-server=%s:%s", proxyData[0], proxyData[1]));

        return  chromeOptions;
    }
}
