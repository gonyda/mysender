package org.bbsk.mysender.fmkorea.controller;

import org.bbsk.mysender.fmkorea.scheduler.FmKoreaScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private final FmKoreaScheduler scheduler;

    public HomeController(FmKoreaScheduler scheduler) {
        this.scheduler = scheduler;
    }

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String home() {
        return "WELCOME my-sender!";
    }

    @GetMapping("/test")
    public String test() {

        scheduler.getFmKoreaCrawlingBySearchKeywordToStock();
        return "Crawling Test";
    }
}
