package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.scrapper.page.PageScrapperResponse;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BrowserService {
    private final Browser browser;
    private final Playwright playwright;

    public BrowserService() {
        playwright = Playwright.create();

        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(
                            List.of(
                            "--disable-gpu",
                            "--disable-dev-shm-usage",
                            "--no-sandbox",
                            "--blink-settings=imagesEnabled=false"
                        )
                    )
        );
    }

    private Browser.NewPageOptions createPageOptions(Map<String, String> headers, String proxy) {
        Browser.NewPageOptions newPageOptions = new Browser.NewPageOptions();

        if (!headers.isEmpty()) {
            newPageOptions.setExtraHTTPHeaders(headers);
        }

        if (proxy != null) {
            newPageOptions.setProxy(proxy);
        }

        return newPageOptions;
    }


    public PageScrapperResponse scrapPage(String pageURL, String dynamicQuerySelector, int timeout, Map<String, String> headers, String proxy) throws IOException {
        Browser.NewPageOptions pageOptions = createPageOptions(headers, proxy);

        return scrapPage(pageURL, dynamicQuerySelector, timeout, pageOptions);
    }

    private PageScrapperResponse scrapPage(String pageURL, String dynamicQuerySelector, int timeout, Browser.NewPageOptions pageOptions) throws IOException {
        Page page = browser.newPage(pageOptions);

        page.route("**/*.{png,jpg,jpeg,gif,webp}", Route::abort); // Block images
        page.route("**/*.css", Route::abort); // Block stylesheets
        page.route("**/*.woff2", Route::abort); // Block web fonts
        page.route("**/ads*", Route::abort); // Block ads

        page.route("**/*", Route::resume);
        Response response = page.navigate(pageURL);

        // Handle page redirects
        if (response.status() == 301) {
            return scrapPage(response.headerValue("Location"), dynamicQuerySelector, timeout, pageOptions);
        }

        // Abort unnecessary awaiting if page response isn't 200 OK
        if (response.status() != 200) {
            throw new IOException(String.format("page: %s, 403 exception", pageURL));
        }

        page.waitForLoadState(LoadState.NETWORKIDLE);

        try {
            page.waitForSelector(dynamicQuerySelector,
                    new Page.WaitForSelectorOptions().setTimeout(timeout * 1000).setState(WaitForSelectorState.ATTACHED));
        } catch (TimeoutError e) {
            throw new IOException(String.format("Dynamic element search timeout exception, page: %s", pageURL));
        } catch (PlaywrightException e) {
            throw new IOException(String.format("page: %s, playwright exception: %s", pageURL, e.getMessage()));
        }

        String body = page.innerHTML("body");

        Map<String, String> cookies = page.context().cookies(response.url()).stream()
                .collect(Collectors.toMap(cookie -> cookie.name, cookie -> cookie.value));
        Map<String, String> headers = response.headers();

        page.close();
        browser.close();
        playwright.close();
        return new PageScrapperResponse(headers, cookies, body);
    }
}
