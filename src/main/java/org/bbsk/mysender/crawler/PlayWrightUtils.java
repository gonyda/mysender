package org.bbsk.mysender.crawler;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import org.springframework.util.Assert;

public class PlayWrightUtils {

    public static BrowserContext getBrowser() {
        Playwright playwright = Playwright.create();
        Assert.notNull(playwright, "## playwright IS NULL");

        Browser browser = playwright.chromium().launch(PlayWrightOptionHolder.INSTANCE.getOptions());
        Assert.notNull(browser, "## browser IS NULL");

        return browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"));
    }

    public static void close(BrowserContext browserContext) {
        browserContext.close();
    }
}
