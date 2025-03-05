package org.bbsk.mysender.fmkorea.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.fmkorea.service.FmKoreaKeywordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fm-korea")
@RequiredArgsConstructor
public class FmKoreaController {

    public final FmKoreaKeywordService fmKoreaSearchKeywordService;

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
}
