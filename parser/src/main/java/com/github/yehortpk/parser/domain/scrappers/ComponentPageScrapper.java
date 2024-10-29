package com.github.yehortpk.parser.domain.scrappers;

import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.models.ScrapperResponseDTO;
import lombok.Cleanup;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.openqa.selenium.devtools.v114.network.Network;
import org.openqa.selenium.devtools.v114.network.model.Headers;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.Optional;

/**
 * Component-based site page scrapper. Uses dynamicElementQuerySelector from {@link PageConnectionParams}
 * to delay page scrapping until the element on the page is loaded. Works on Selenium
 */
@Component
public class ComponentPageScrapper implements PageScrapper {
    @Override
    public ScrapperResponseDTO scrapPage(PageConnectionParams pageConnectionParams) {
        ChromeOptions chromeOptions;
        if (pageConnectionParams.getProxy() != null) {
            chromeOptions = createChromeOptions(pageConnectionParams.getProxy());
        } else {
            chromeOptions = createChromeOptions();
        }

        @Cleanup ChromeDriver driver = createWebDriver(chromeOptions);
        String finalPageUrl = constructURLWithData(pageConnectionParams.getPageUrl(), pageConnectionParams.getData());

        return scrapPage(finalPageUrl, driver, pageConnectionParams.getDynamicElementQuerySelector());
    }

    /**
     * Scraps page with specific url and specific {@link WebDriver}
     * @param pageUrl URL to the page
     * @param driver GoogleChrome web driver for Selenium parser
     * @param dynamicElementQuerySelector element query selector for component section loading delay
     * @return page HTML
     */
    private ScrapperResponseDTO scrapPage(String pageUrl, ChromeDriver driver, String dynamicElementQuerySelector) {
        synchronized (ComponentPageScrapper.class) {
            DevTools devTools = driver.getDevTools();
            devTools.createSession();
            devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

            AtomicReference<Headers> headers = new AtomicReference<>();
            devTools.addListener(Network.responseReceived(), responseReceived -> {
                String requestUrl = responseReceived.getResponse().getUrl();

                if (requestUrl.equals(pageUrl)) {
                    headers.set(responseReceived.getResponse().getHeaders());
                }
            });


            driver.get(pageUrl);

            new FluentWait<WebDriver>(driver)
                    .withTimeout(Duration.ofSeconds(30))
                    .pollingEvery(Duration.ofMillis(200))
                    .ignoring(NoSuchElementException.class, TimeoutException.class).ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(dynamicElementQuerySelector)));

            Map<String, String> headersMap = new HashMap<>();
            if (headers.get() != null) {
                headersMap = headers.get().toJson().entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().toString()
                ));
            }

            return new ScrapperResponseDTO(headersMap, driver.getPageSource());
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

    @Value("${webdriver.chrome.driver}")
    private String chromeDriverPath;

    /**
     * Create GoogleChrome web driver for Selenium with specific path in the system
     * @param chromeOptions web driver options
     * @return web driver
     */
    private ChromeDriver createWebDriver(ChromeOptions chromeOptions) {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        return new ChromeDriver(chromeOptions);
    }

    /**
     * Construct GET url with the data map as query params
     * @param pageUrl base url without query params
     * @param data data for query params
     * @return final url
     */
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

    /**
     * Creates Google Chrome web driver options without proxy
     * @return web driver options
     */
    private ChromeOptions createChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless", "--disable-dev-shm-usage", "--no-sandbox");
        chromeOptions.addArguments("window-size=1920,1080");
        chromeOptions.setExperimentalOption("prefs", new java.util.HashMap<String, Object>() {{
            put("profile.managed_default_content_settings.javascript", 1); // Ensure JavaScript is enabled
        }});

        chromeOptions.setBinary(chromeBinaryPath);

        return chromeOptions;
    }

    /**
     * Creates Google Chrome web driver options with proxy
     * @param proxy proxy object
     * @return web driver options
     */
    private ChromeOptions createChromeOptions(Proxy proxy) {
        ChromeOptions chromeOptions = createChromeOptions();
        chromeOptions.addArguments("window-size=1920,1080");
        chromeOptions.setExperimentalOption("prefs", new java.util.HashMap<String, Object>() {{
            put("profile.managed_default_content_settings.javascript", 1); // Ensure JavaScript is enabled
        }});
        String[] proxyData = retrieveDataFromProxy(proxy);
        chromeOptions.addArguments(String.format("--proxy-server=%s:%s", proxyData[0], proxyData[1]));

        return  chromeOptions;
    }
}
