package org.bbsk.mysender.fmkorea.controller;

import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.fmkorea.scheduler.FmKoreaScheduler;
import org.bbsk.mysender.fmkorea.service.FmKoreaKeywordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fm-korea")
public class FmKoreaController {

    private final FmKoreaKeywordService fmKoreaSearchKeywordService;
    private final FmKoreaScheduler scheduler;

    public FmKoreaController(FmKoreaKeywordService fmKoreaSearchKeywordService, FmKoreaScheduler scheduler) {
        this.fmKoreaSearchKeywordService = fmKoreaSearchKeywordService;
        this.scheduler = scheduler;
    }

    @GetMapping("/get")
    public String get() {
        return StringUtils.join(fmKoreaSearchKeywordService.getKeywordList(), ", ");
    }

    @GetMapping("/add/{keyword}")
    public String addKeyword(@PathVariable String keyword) {
        return StringUtils.join(fmKoreaSearchKeywordService.addKeyword(keyword), ", ");
    }

    @GetMapping("/remove/{keyword}")
    public String removeKeyword(@PathVariable String keyword) {
        return StringUtils.join(fmKoreaSearchKeywordService.removeKeyword(keyword), ", ");
    }

    @GetMapping("/test")
    public String test() {
        scheduler.getFmKoreaCrawlingByPopularToStock();
        return "ok";
    }

    /**
     * 키워드 검색
     * @return
     */
    @GetMapping("/")
    public String home() {
        scheduler.getFmKoreaCrawlingBySearchKeywordToStock();
        return "WELCOME my-sender!";
    }
}
