package org.bbsk.mysender.fmkorea.scheduler;

import lombok.RequiredArgsConstructor;
import org.bbsk.mysender.crawler.SeleniumUtils;
import org.bbsk.mysender.fmkorea.service.FmKoreaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FmKoreaScheduler {

    private final FmKoreaService fmKoreaService;

    @Scheduled(cron = "0 49 * * * ?")
    public void getFmKoreaSearchKeywordByStock() {
        fmKoreaService.getFmKoreaSearchKeywordByStock(SeleniumUtils.getChromeDriver());
    }
}
