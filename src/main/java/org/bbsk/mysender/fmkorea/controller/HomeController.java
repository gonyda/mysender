package org.bbsk.mysender.fmkorea.controller;

import org.bbsk.mysender.fmkorea.scheduler.FmKoreaScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private final FmKoreaScheduler scheduler;

    public HomeController(FmKoreaScheduler scheduler) {
        this.scheduler = scheduler;
    }

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
