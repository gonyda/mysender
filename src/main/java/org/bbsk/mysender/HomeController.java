package org.bbsk.mysender;

import org.bbsk.mysender.fmkorea.scheduler.FmKoreaScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final FmKoreaScheduler scheduler;

    public HomeController(FmKoreaScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/test")
    public void test() {
        scheduler.getFmKoreaCrawlingByPopularToStock();
    }
}
