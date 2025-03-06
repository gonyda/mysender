package org.bbsk.mysender.fmkorea.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.crawler.SeleniumUtils;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.bbsk.mysender.fmkorea.scheduler.FmKoreaScheduler;
import org.bbsk.mysender.fmkorea.service.FmKoreaCrawlingByPopularService;
import org.bbsk.mysender.fmkorea.service.FmKoreaKeywordService;
import org.bbsk.mysender.fmkorea.template.FmKoreaMailTemplateService;
import org.bbsk.mysender.gmail.service.GmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/fm-korea")
@RequiredArgsConstructor
@Slf4j
public class FmKoreaController {

    public final FmKoreaKeywordService fmKoreaSearchKeywordService;

    public final FmKoreaScheduler scheduler;

    @GetMapping("/get")
    public String get() {
        return StringUtils.join(fmKoreaSearchKeywordService.getKeywordList(), ", ");
    }

    @GetMapping("/add/{keyword}")
    public String addKeyword(@PathVariable String keyword) {
        fmKoreaSearchKeywordService.addKeyword(keyword);
        return StringUtils.join(fmKoreaSearchKeywordService.getKeywordList(), ", ");
    }

    @GetMapping("/remove/{keyword}")
    public String removeKeyword(@PathVariable String keyword) {
        fmKoreaSearchKeywordService.removeKeyword(keyword);
        return StringUtils.join(fmKoreaSearchKeywordService.getKeywordList(), ", ");
    }

    @GetMapping("/test")
    public String test() {
        scheduler.getFmKoreaCrawlingByPopularToStock();
        return "ok";
    }
}
