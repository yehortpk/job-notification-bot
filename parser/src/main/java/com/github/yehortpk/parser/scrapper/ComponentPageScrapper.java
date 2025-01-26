package com.github.yehortpk.parser.scrapper;

import com.github.yehortpk.parser.models.PageConnectionParams;
import com.github.yehortpk.parser.services.RequestProxyService;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.remote.ErrorHandler;
import org.openqa.selenium.remote.http.ConnectionFailedException;
import org.openqa.selenium.safari.ConnectionClosedException;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import org.openqa.selenium.devtools.v114.network.Network;
import org.openqa.selenium.devtools.v114.network.model.Headers;

import java.io.IOException;
import java.net.Proxy;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.Optional;

/**
 * Component-based site page scrapper. Uses dynamicElementQuerySelector from {@link PageConnectionParams}
 * to delay page scrapping until the element on the page is loaded. Works on Selenium
 */
@Slf4j
@RequiredArgsConstructor
public class ComponentPageScrapper implements PageScrapper {
    private final String dynamicElementQuerySelector;
    private RequestProxyService requestProxyService;

    @Override
    public PageScrapperResponse scrapPage(PageConnectionParams pageConnectionParams) throws IOException {
        Proxy proxy = pageConnectionParams.getProxy();
        if (proxy != null) {
            requestProxyService = new RequestProxyService(proxy);
        } else {
            requestProxyService = new RequestProxyService();
        }

        requestProxyService.addHeaders(pageConnectionParams.getHeaders());
        requestProxyService.startService();

        ChromeOptions chromeOptions = createChromeOptions();

        @Cleanup ChromeDriver driver = createWebDriver(chromeOptions);

        String finalPageUrl = constructURLWithData(pageConnectionParams.getPageUrl(), pageConnectionParams.getData());
        pageConnectionParams.setPageUrl(finalPageUrl);

        PageScrapperResponse pageScrapperResponse = parsePage(driver, pageConnectionParams);

        requestProxyService.stopService();
        return pageScrapperResponse;
    }

    /**
     * Scraps page with specific url and specific {@link WebDriver}
     * @param driver GoogleChrome web driver for Selenium parser
     * @param pageConnectionParams element query selector for component section loading delay
     * @return page HTML
     */
    private PageScrapperResponse parsePage(ChromeDriver driver, PageConnectionParams pageConnectionParams) throws IOException {
        DevTools devTools = driver.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        AtomicReference<Headers> headers = new AtomicReference<>();
        String pageUrl = pageConnectionParams.getPageUrl();
        devTools.addListener(Network.responseReceived(), responseReceived -> {
            String requestUrl = responseReceived.getResponse().getUrl();
            if (requestUrl.equals(pageUrl)) {
                headers.set(responseReceived.getResponse().getHeaders());
            }
        });

        log.info("Connect to the page {}, proxy: {},  data: {}, headers: {}",
                pageConnectionParams.getPageUrl(),
                pageConnectionParams.getProxy(),
                pageConnectionParams.getData(),
                pageConnectionParams.getHeaders()
        );
        driver.get(pageUrl);

        log.info("Connection to the page {}, proxy: {},  data: {}, headers: {} was established",
                pageConnectionParams.getPageUrl(),
                pageConnectionParams.getProxy(),
                pageConnectionParams.getData(),
                pageConnectionParams.getHeaders()
        );

        Wait<ChromeDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.of(pageConnectionParams.getTimeoutSec(), ChronoUnit.SECONDS))
                .pollingEvery(Duration.of(5, ChronoUnit.SECONDS))
                .ignoring(NoSuchElementException.class)
                .ignoring(ConnectionClosedException.class)
                .ignoring(ConnectionFailedException.class)
                .ignoring(ErrorHandler.UnknownServerException.class);

        try {
            wait.until(dr -> dr.findElement(By.cssSelector(dynamicElementQuerySelector)));
        } catch ( org.openqa.selenium.TimeoutException te) {
            throw new IOException(String.format("Dynamic element search timeout exception, page: %s", pageUrl));
        }

        Map<String, String> headersMap = new HashMap<>();
        if (headers.get() != null) {
            headersMap = headers.get().toJson().entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().toString()
            ));
        }
        Map<String, String> cookies = driver.manage().getCookies().stream().collect(Collectors.toMap(Cookie::getName, Cookie::getValue));

        return new PageScrapperResponse(headersMap, cookies, driver.getPageSource());
    }

    /**
     * Create GoogleChrome web driver for Selenium with specific path in the system
     * @param chromeOptions web driver options
     * @return web driver
     */
    private ChromeDriver createWebDriver(ChromeOptions chromeOptions) {
        final String chromeDriverPath = "/usr/local/bin/chromedriver";
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
            StringBuilder pageUrlBuilder = new StringBuilder(pageUrl + "?");
            for (Map.Entry<String, String> dataES : data.entrySet()) {
                if (dataES.getKey().endsWith("[]")) {
                    for (String valuePart : dataES.getValue().split(",")) {
                        pageUrlBuilder.append(dataES.getKey()).append("=").append(valuePart).append("&");
                    }
                } else {
                    pageUrlBuilder.append(dataES.getKey()).append("=").append(dataES.getValue()).append("&");
                }

            }
            pageUrl = pageUrlBuilder.toString();
        }

        return pageUrl;

    }

    /**
     * Creates Google Chrome web driver options without proxy
     * @return web driver options
     */
    private ChromeOptions createChromeOptions() {
        final String chromeBinaryPath = "/usr/bin/google-chrome";
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(
                "--headless",
                "--disable-gpu",
                "--disable-dev-shm-usage",
                "--disable-extensions",
                "--no-sandbox",
                "--disable-infobars",
                "--disable-notifications",
                "--blink-settings=imagesEnabled=false",
                "--disk-cache-size=0",
                "--enable-cookies",
                "--ignore-certificate-errors"
        );
        chromeOptions.setBinary(chromeBinaryPath);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.images", 2);
        prefs.put("profile.managed_default_content_settings.images", 2);
        prefs.put("profile.managed_default_content_settings.javascript", 1);

        chromeOptions.setExperimentalOption("prefs", prefs);
        chromeOptions.setProxy(ClientUtil.createSeleniumProxy(requestProxyService.getProxy()));

        return chromeOptions;
    }
}
