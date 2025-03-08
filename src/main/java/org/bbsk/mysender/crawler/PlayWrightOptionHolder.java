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
                        "--disable-setuid-sandbox",
                        "--no-first-run",
                        "--no-zygote",
                        "--single-process",
                        "--disable-extensions",
                        "--disable-background-networking",
                        "--disable-background-timer-throttling",
                        "--disable-renderer-backgrounding",
                        "--disable-sync",
                        "--disable-translate",
                        "--disable-infobars",
                        "--disable-notifications",
                        "--disable-default-apps",
                        "--mute-audio"
                ));
    }

    public BrowserType.LaunchOptions getOptions() {
        return options;
    }
}
