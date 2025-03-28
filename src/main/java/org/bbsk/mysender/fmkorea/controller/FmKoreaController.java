package org.bbsk.mysender.fmkorea.controller;

import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.fmkorea.service.FmKoreaKeywordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fm-korea")
public class FmKoreaController {

    private final FmKoreaKeywordService fmKoreaSearchKeywordService;

    public FmKoreaController(FmKoreaKeywordService fmKoreaSearchKeywordService) {
        this.fmKoreaSearchKeywordService = fmKoreaSearchKeywordService;
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

    @GetMapping("/removeAll")
    public String removeAll() {
        return StringUtils.join(fmKoreaSearchKeywordService.removeAll(), ", ");
    }
}
