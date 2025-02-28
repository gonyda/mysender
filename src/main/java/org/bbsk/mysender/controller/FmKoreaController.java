package org.bbsk.mysender.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.fmkorea.constant.FmKoreaSearchKeyword;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fm-korea")
@RequiredArgsConstructor
public class FmKoreaController {

    private final FmKoreaSearchKeyword fmKoreaSearchKeywordService;

    @GetMapping("/keyword-list")
    public String test() {
        List<String> keywordList = fmKoreaSearchKeywordService. getKeywordList();
        return StringUtils.join(keywordList, ", ");
    }
}
