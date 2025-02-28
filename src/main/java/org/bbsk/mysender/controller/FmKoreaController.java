package org.bbsk.mysender.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.fmkorea.entity.FmKoreaSearchKeyword;
import org.bbsk.mysender.fmkorea.repository.FmKoreaSearchKeywordRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fm-korea")
@RequiredArgsConstructor
public class FmKoreaController {

    private final FmKoreaSearchKeywordRepository fmKoreaSearchKeywordRepository;

    @GetMapping("/keyword-list")
    public String test() {
        List<FmKoreaSearchKeyword> keywordList = fmKoreaSearchKeywordRepository.getFmKoreaSearchKeywordByUseYn("Y");
        return StringUtils.join(keywordList.stream().map(FmKoreaSearchKeyword::getKeyword).toArray(), ", ");
    }
}
