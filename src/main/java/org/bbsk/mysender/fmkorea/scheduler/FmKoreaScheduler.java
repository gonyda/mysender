package org.bbsk.mysender.fmkorea.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.crawler.SeleniumUtils;
import org.bbsk.mysender.fmkorea.dto.FmKoreaMailDto;
import org.bbsk.mysender.fmkorea.entity.FmKoreaSearchKeyword;
import org.bbsk.mysender.fmkorea.repository.FmKoreaSearchKeywordRepository;
import org.bbsk.mysender.fmkorea.service.FmKoreaService;
import org.bbsk.mysender.gmail.service.GmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FmKoreaScheduler {

    private final FmKoreaService fmKoreaService;
    private final GmailService gmailService;
    private final FmKoreaSearchKeywordRepository fmKoreaSearchKeywordRepository;

    @Scheduled(cron = "0 35 * * * ?")
    public void getFmKoreaSearchKeywordByStock() {
        List<List<FmKoreaMailDto>> mailList = new ArrayList<>();
        List<FmKoreaSearchKeyword> keywordList = fmKoreaSearchKeywordRepository.getFmKoreaSearchKeywordByUseYn("Y");

        log.info("## Start");
        log.info("## keywordList: {}", StringUtils.join(keywordList.stream().map(FmKoreaSearchKeyword::getKeyword).toArray(), ", "));

        for (FmKoreaSearchKeyword entity : keywordList) {
            log.info("## Keyword : {}", entity.getKeyword());
            mailList.add(fmKoreaService.getFmKoreaSearchKeywordByStock(SeleniumUtils.getChromeDriver(), entity.getKeyword()));
        }

        if(!mailList.isEmpty()) {
            // TODO 메일 발송
            //gmailService.sendHtmlEmail("bbsk3939@gmail.com", "연습입니다앙~", "발송되냐?");
        }

        log.info("## End");
    }
}
