package org.bbsk.mysender.crawler;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;

public enum ChromeOptionsHolder {

    INSTANCE;

    private final ChromeOptions chromeOptions;

    ChromeOptionsHolder() {
        this.chromeOptions = create();
    }

    private static ChromeOptions create() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars"); // 정보 표시줄 비활성화
        options.addArguments("--disable-extensions"); // 확장 프로그램 비활성화
        options.addArguments("--headless"); // 백그라운드 실행
        options.addArguments("--disable-gpu"); // GPU 비활성화
        options.addArguments("--no-sandbox"); // 샌드박스 모드 비활성화
        options.addArguments("--disable-dev-shm-usage"); // /dev/shm 사용 비활성화

        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        return options;
    }

    public ChromeOptions get() {
        return this.chromeOptions;
    }
}