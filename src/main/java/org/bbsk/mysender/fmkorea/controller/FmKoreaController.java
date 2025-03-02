package org.bbsk.mysender.fmkorea.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.fmkorea.jpa.entity.FmKoreaSearchKeyword;
import org.bbsk.mysender.fmkorea.jpa.service.FmKoreaJpaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fm-korea")
@RequiredArgsConstructor
public class FmKoreaController {

    private final FmKoreaJpaService fmKoreaSearchKeywordService;

    @GetMapping("/get")
    public String get() {
        List<FmKoreaSearchKeyword> keywordList = fmKoreaSearchKeywordService.getFmKoreaSearchKeywordByUseYn("Y");
        return StringUtils.join(keywordList.stream().map(FmKoreaSearchKeyword::getKeyword).toList(), ", ");
    }

    @GetMapping("/add/{keyword}")
    public String addKeyword(@PathVariable String keyword) {
        fmKoreaSearchKeywordService.addFmKoreaSearchKeyword(keyword);
        List<FmKoreaSearchKeyword> keywordList = fmKoreaSearchKeywordService.getFmKoreaSearchKeywordByUseYn("Y");
        return StringUtils.join(keywordList.stream().map(FmKoreaSearchKeyword::getKeyword).toList(), ", ");
    }

    @GetMapping("/remove/{keyword}")
    public String removeKeyword(@PathVariable String keyword) {
        fmKoreaSearchKeywordService.removeFmKoreaSearchKeyword(keyword);
        List<FmKoreaSearchKeyword> keywordList = fmKoreaSearchKeywordService.getFmKoreaSearchKeywordByUseYn("Y");
        return StringUtils.join(keywordList.stream().map(FmKoreaSearchKeyword::getKeyword).toList(), ", ");
    }
}
