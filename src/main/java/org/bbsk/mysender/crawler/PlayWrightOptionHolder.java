package org.bbsk.mysender.crawler;

import com.microsoft.playwright.BrowserType;

import java.util.Arrays;

public enum PlayWrightOptionHolder {

    INSTANCE;

    private final BrowserType.LaunchOptions options;

    PlayWrightOptionHolder() {
        this.options = create();
    }

    private static BrowserType.LaunchOptions create() {
        return new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setArgs(Arrays.asList(
                        "--disable-gpu",
                        "--disable-dev-shm-usage",
                        "--no-sandbox",
                        "--disable-extensions",
                        "--disable-infobars",
                        "--disable-blink-features=AutomationControlled"
                ));
    }

    public BrowserType.LaunchOptions getOptions() {
        return options;
    }
}
