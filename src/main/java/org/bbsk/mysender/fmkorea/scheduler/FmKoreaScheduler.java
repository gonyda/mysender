package org.bbsk.mysender.fmkorea.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.crawler.SeleniumUtils;
import org.bbsk.mysender.fmkorea.dto.FmKoreaMailDto;
import org.bbsk.mysender.fmkorea.jpa.entity.FmKoreaSearchKeyword;
import org.bbsk.mysender.fmkorea.jpa.service.FmKoreaJpaService;
import org.bbsk.mysender.fmkorea.service.FmKoreaCrawlingByKeywordSearchService;
import org.bbsk.mysender.fmkorea.service.FmKoreaCrawlingByPopularService;
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

    private final FmKoreaCrawlingByKeywordSearchService fmKoreaCrawlingByKeywordSearchService;
    private final FmKoreaCrawlingByPopularService fmKoreaCrawlingByPopularService;

    private final GmailService gmailService;
    private final FmKoreaMailTemplateService fmKoreaMailTemplateService;
    private final FmKoreaJpaService fmKoreaJpaService;

    /**
     * 주식 게시판
     * 키워드 검색
     * 크롤링 스케줄러
     */
    @Scheduled(cron = "0 0 */2 * * ?")
    public void getFmKoreaCrawlingBySearchKeywordToStock() {
        log.info("## Search Keyword Start");
        LocalDateTime now = LocalDateTime.now();

        List<FmKoreaSearchKeyword> keywordList = fmKoreaJpaService.getFmKoreaSearchKeywordByUseYn("Y");
        log.info("## Keyword List: {}", StringUtils.join(keywordList.stream().map(FmKoreaSearchKeyword::getKeyword).toList(), ", "));

        List<List<FmKoreaMailDto>> mailList = new ArrayList<>();
        keywordList.forEach(entity ->
                mailList.add(
                        fmKoreaCrawlingByKeywordSearchService.getFmKoreaCrawlingBySearchKeywordToStock(
                                SeleniumUtils.getChromeDriver()
                                , entity.getKeyword()
                                , now
                                , 2
                        )
                )
        );

        for (List<FmKoreaMailDto> mail : mailList) {
            gmailService.sendEmail(
                    "bbsk3939@gmail.com"
                    , StringUtils.join(mail.get(0).getKeyword(), " 검색결과 ", mail.size(), "개")
                    , fmKoreaMailTemplateService.getHtmlForSendMail(mail)
            );
            log.info("## Send Email: {}", mail.get(0).getKeyword());
        }

        log.info("## Search Keyword End");
    }


    /**
     * 주식 게시판
     * 인기글
     * 크롤링 스케줄러
     */
    @Scheduled(cron = "0 0 1,3,5,7,9,11,13,15,17,19,21,23 * * ?")
    public void getFmKoreaCrawlingByPopularToStock() {
        log.info("## Popular Start");
        LocalDateTime now = LocalDateTime.now();

        List<FmKoreaMailDto> mailList =
                fmKoreaCrawlingByPopularService.getFmKoreaCrawlingByPopularToStock(
                        SeleniumUtils.getChromeDriver()
                        , now
                        , 2
                );


        gmailService.sendEmail(
                "bbsk3939@gmail.com"
                , StringUtils.join("인기글 검색결과 ", mailList.size(), "개")
                , fmKoreaMailTemplateService.getHtmlForSendMail(mailList));

        log.info("## Send Email: 인기글");

        log.info("## Popular End");
    }


}
