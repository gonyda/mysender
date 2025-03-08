package org.bbsk.mysender.crawler;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.springframework.util.Assert;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PlayWrightUtils {

    private static Playwright playwright;
    private static Browser browser;
    private static final Lock lock = new ReentrantLock(); // Thread-safe

    public static BrowserContext getBrowser() {
        if (playwright == null) {
            playwright = Playwright.create();
        }
        Assert.notNull(playwright, "## playwright IS NULL");

        if (browser == null) {
            browser = playwright.chromium().launch(PlayWrightOptionHolder.INSTANCE.getOptions());
        }
        Assert.notNull(browser, "## browser IS NULL");

        return browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36"));
    }

    public static void close(BrowserContext browserContext, Page mainPage) {
        if (mainPage != null) {
            mainPage.close();
        }
        if (browserContext != null) {
            browserContext.close();
        }
        if (browser != null) {
            browser.close();
            browser = null; // Ensure proper cleanup
        }
        if (playwright != null) {
            playwright.close();
            playwright = null; // Ensure proper cleanup
        }
    }
}