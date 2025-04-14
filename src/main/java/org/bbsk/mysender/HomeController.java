package org.bbsk.mysender;

import org.bbsk.mysender.fmkorea.scheduler.FmKoreaScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final FmKoreaScheduler fmKoreaScheduler;

    public HomeController(FmKoreaScheduler fmKoreaScheduler) {
        this.fmKoreaScheduler = fmKoreaScheduler;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/stock")
    public String stock() {
        return "stock";
    }

    @GetMapping("/test")
    public void test() {
        fmKoreaScheduler.getFmKoreaCrawlingByPopularToStock();
    }
}
