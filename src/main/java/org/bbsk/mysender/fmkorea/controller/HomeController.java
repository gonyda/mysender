package org.bbsk.mysender.fmkorea.controller;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import org.bbsk.mysender.crawler.PlayWrightUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {


    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String home() {
        return "WELCOME my-sender!";
    }

    @GetMapping("/test")
    public String test() {

        Browser browser = PlayWrightUtils.getBrowser();
        Page page = browser.newPage();
        String url = page.navigate("https://www.naver.com").url();

        log.info("url : {}", url);
        
        return "Crawling Test";
    }
}
