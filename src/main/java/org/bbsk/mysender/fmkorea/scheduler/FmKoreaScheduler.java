package org.bbsk.mysender.fmkorea.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.crawler.SeleniumUtils;
import org.bbsk.mysender.fmkorea.constant.FmKoreaSearchKeyword;
import org.bbsk.mysender.fmkorea.dto.FmKoreaMailDto;
import org.bbsk.mysender.fmkorea.service.FmKoreaService;
import org.bbsk.mysender.fmkorea.template.FmKoreaMailTemplateService;
import org.bbsk.mysender.gmail.service.GmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FmKoreaScheduler {

    private final FmKoreaService fmKoreaService;
    private final GmailService gmailService;
    private final FmKoreaMailTemplateService fmKoreaMailTemplateService;
    private final FmKoreaSearchKeyword searchKeyword;

    /**
     * 크롤링 스케줄러
     */
//    @Scheduled(cron = "0 0 */2 * * ?")
    @Scheduled(cron = "* 9 * * * ?")
    public void getFmKoreaCrawlingBySearchKeywordToStock() {
        log.info("## Start");
        LocalDateTime now = LocalDateTime.now();

        List<String> keywordList = searchKeyword.getKeywordList();
        log.info("## Keyword List: {}", StringUtils.join(keywordList, ", "));

        List<List<FmKoreaMailDto>> mailList = new ArrayList<>();
        keywordList.forEach(keyword ->
                mailList.add(
                        fmKoreaService.getFmKoreaCrawlingBySearchKeywordToStock(
                                SeleniumUtils.getChromeDriver()
                                , keyword
                                , now
                        )
                )
        );
        log.info("## End Crawling");

        for (List<FmKoreaMailDto> mail : mailList) {
            gmailService.sendEmail(
                    "bbsk3939@gmail.com"
                    , StringUtils.join(mail.get(0).getKeyword(), " 검색결과 ", mail.size(), "개")
                    , fmKoreaMailTemplateService.getHtmlForSendMail(mail)
            );
            log.info("## Send Email: {}", mail.get(0).getKeyword());
        }

        log.info("## End");
    }



}
