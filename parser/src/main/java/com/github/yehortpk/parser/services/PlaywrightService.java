package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.scrapper.PageScrapperResponse;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlaywrightService {
    private final Browser browser;
    private final Playwright playwright;

    public PlaywrightService() {
        playwright = Playwright.create();
        System.setProperty("playwright.browser.installation.strategy", "SKIP");
        System.setProperty("playwright.skip.browser.download", "true");

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)
                        .setArgs(List.of(
                            "--disable-gpu",
                            "--disable-dev-shm-usage",
                            "--no-sandbox",
                            "--blink-settings=imagesEnabled=false"
                        ))
        );
    }

    private BrowserContext createContext(Map<String, String> headers, String proxy) {
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();

        if (!headers.isEmpty()) {
            contextOptions.setExtraHTTPHeaders(headers);
        }

        if (proxy != null) {
            contextOptions.setProxy(proxy);
        }

        return browser.newContext(contextOptions);
    }


    public PageScrapperResponse scrapPage(String pageURL, String dynamicQuerySelector, int timeout, Map<String, String> headers, String proxy) throws IOException {
        BrowserContext context = createContext(headers, proxy);

        return scrapPage(pageURL, dynamicQuerySelector, timeout, context);
    }

    private PageScrapperResponse scrapPage(String pageURL, String dynamicQuerySelector, int timeout, BrowserContext context) throws IOException {
        Page page = context.newPage();

        context.route("**/*", Route::resume);

//        page.route("**/*.{png,jpg,jpeg,gif,webp}", Route::abort); // Block images
//        page.route("**/*.css", Route::abort); // Block stylesheets
//        page.route("**/*.woff2", Route::abort); // Block web fonts
//        page.route("**/ads*", Route::abort); // Block ads

        Response response = page.navigate(pageURL);
        page.waitForLoadState(LoadState.NETWORKIDLE);

        try {
            page.waitForSelector(dynamicQuerySelector,
                    new Page.WaitForSelectorOptions().setTimeout(timeout * 1000));
        } catch (PlaywrightException e) {
            throw new IOException(String.format("Dynamic element search timeout exception, page: %s", pageURL));
        }

        String body = page.innerHTML("body");

        Map<String, String> cookies = context.cookies(response.url()).stream()
                .collect(Collectors.toMap(cookie -> cookie.name, cookie -> cookie.value));
        Map<String, String> headers = response.headers();

        page.close();
        browser.close();
        playwright.close();
        return new PageScrapperResponse(headers, cookies, body);
    }
}
