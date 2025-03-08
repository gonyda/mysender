package org.bbsk.mysender.crawler;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import org.springframework.util.Assert;

public class PlayWrightUtils {

    public static Browser getBrowser() {
        Playwright playwright = Playwright.create();
        Assert.notNull(playwright, "## playwright IS NULL");

        Browser browser = playwright.chromium().launch(PlayWrightOptionHolder.INSTANCE.getOptions());
        Assert.notNull(browser, "## browser IS NULL");

        return browser;
    }
}
