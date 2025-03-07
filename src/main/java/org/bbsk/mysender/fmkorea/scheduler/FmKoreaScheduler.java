package org.bbsk.mysender.fmkorea.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.crawler.SeleniumUtils;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.bbsk.mysender.fmkorea.service.FmKoreaCrawlingByKeywordSearchService;
import org.bbsk.mysender.fmkorea.service.FmKoreaCrawlingByPopularService;
import org.bbsk.mysender.fmkorea.service.FmKoreaKeywordService;
import org.bbsk.mysender.fmkorea.template.FmKoreaMailTemplateService;
import org.bbsk.mysender.gmail.service.GmailService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class FmKoreaScheduler {

    private final FmKoreaCrawlingByKeywordSearchService fmKoreaCrawlingByKeywordSearchService;
    private final FmKoreaCrawlingByPopularService fmKoreaCrawlingByPopularService;

    private final GmailService gmailService;
    private final FmKoreaMailTemplateService fmKoreaMailTemplateService;
    private final FmKoreaKeywordService fmKoreaKeywordService;

    /**
     * 주식 게시판
     * 키워드 검색
     * 크롤링 스케줄러
     */
    @Scheduled(cron = "0 0 */2 * * ?")
    public void getFmKoreaCrawlingBySearchKeywordToStock() {
        log.info("## Search Keyword Start");
        LocalDateTime now = LocalDateTime.now();

        List<String> keywordList = fmKoreaKeywordService.getKeywordList();
        log.info("## Keyword List: {}", StringUtils.join(keywordList, ", "));

        List<List<FmKoreaArticleDto>> mailList = new ArrayList<>(); // List By Keyword
        keywordList.forEach(keyword ->
                mailList.add(
                        fmKoreaCrawlingByKeywordSearchService.getFmKoreaCrawlingBySearchKeywordToStock(
                                SeleniumUtils.getChromeDriver()
                                , keyword
                                , now
                                , 2
                        )
                )
        );

        for (List<FmKoreaArticleDto> articleList : mailList) {
            gmailService.sendEmail(
                    "bbsk3939@gmail.com"
                    , StringUtils.join(articleList.get(0).getKeyword(), " 검색결과 ", articleList.size(), "개")
                    , fmKoreaMailTemplateService.getHtmlForSendMail(articleList)
            );
            log.info("## Send Email: {}", articleList.get(0).getKeyword());
        }

        log.info("## Search Keyword End");
    }


    /**
     * 주식 게시판
     * 인기글
     * 크롤링 스케줄러
     */
    @Scheduled(cron = "0 0 */3 * * ?")
    public void getFmKoreaCrawlingByPopularToStock() {
        log.info("## Popular Start");
        LocalTime now = LocalTime.now();

        List<FmKoreaArticleDto> articleList =
                fmKoreaCrawlingByPopularService.getFmKoreaCrawlingByPopularToStock(
                        SeleniumUtils.getChromeDriver()
                        , now
                        , 180L
                );

        if (!articleList.isEmpty()) {
            gmailService.sendEmail(
                    "bbsk3939@gmail.com"
                    , StringUtils.join("인기글 검색결과 ", articleList.size(), "개")
                    , fmKoreaMailTemplateService.getHtmlForSendMail(articleList));

            log.info("## Send Email: 인기글");
        }

        log.info("## Popular End");
    }
}
