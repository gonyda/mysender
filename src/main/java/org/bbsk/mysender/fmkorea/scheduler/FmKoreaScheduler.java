package org.bbsk.mysender.fmkorea.scheduler;

import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.crawler.PlayWrightUtils;
import org.bbsk.mysender.crawler.SeleniumUtils;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.bbsk.mysender.fmkorea.service.keyword.FmKoreaCrawlingByKeywordSearchService;
import org.bbsk.mysender.fmkorea.service.popular.FmKoreaCrawlingByPopularService;
import org.bbsk.mysender.fmkorea.service.FmKoreaKeywordService;
import org.bbsk.mysender.fmkorea.template.FmKoreaMailTemplateService;
import org.bbsk.mysender.gmail.service.GmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@EnableScheduling
public class FmKoreaScheduler {

    private static final Logger log = LoggerFactory.getLogger(FmKoreaScheduler.class);

    private final FmKoreaCrawlingByKeywordSearchService fmKoreaCrawlingByKeywordSearchService;
    private final FmKoreaCrawlingByPopularService fmKoreaCrawlingByPopularService;
    private final GmailService gmailService;
    private final FmKoreaMailTemplateService fmKoreaMailTemplateService;
    private final FmKoreaKeywordService fmKoreaKeywordService;

    public FmKoreaScheduler(FmKoreaCrawlingByKeywordSearchService fmKoreaCrawlingByKeywordSearchService, FmKoreaCrawlingByPopularService fmKoreaCrawlingByPopularService, GmailService gmailService, FmKoreaMailTemplateService fmKoreaMailTemplateService, FmKoreaKeywordService fmKoreaKeywordService) {
        this.fmKoreaCrawlingByKeywordSearchService = fmKoreaCrawlingByKeywordSearchService;
        this.fmKoreaCrawlingByPopularService = fmKoreaCrawlingByPopularService;
        this.gmailService = gmailService;
        this.fmKoreaMailTemplateService = fmKoreaMailTemplateService;
        this.fmKoreaKeywordService = fmKoreaKeywordService;
    }

    /**
     * 주식 게시판
     * 키워드 검색
     * 크롤링 스케줄러
     */
    @Scheduled(cron = "0 0 */2 * * ?")
    public void getFmKoreaCrawlingBySearchKeywordToStock() {
        log.info("## Search Keyword Start");

        List<String> keywordList = fmKoreaKeywordService.getKeywordList();
        log.info("## Keyword List: {}", StringUtils.join(keywordList, ", "));

        List<List<FmKoreaArticleDto>> mailListGroupByKeyword = keywordList.stream()
                        .map(keyword -> fmKoreaCrawlingByKeywordSearchService
                                .getFmKoreaCrawlingBySearchKeywordToStock(keyword, LocalDateTime.now(), 2))
                        .toList();


        Optional.of(mailListGroupByKeyword)
                .ifPresent(mailList -> {
                    mailList.stream()
                            .filter(list -> !list.isEmpty())
                            .forEach(crawledArticles -> {
                                gmailService.sendEmail(
                                        "bbsk3939@gmail.com"
                                        , StringUtils.join(crawledArticles.get(0).getKeyword(), " 검색결과 ", crawledArticles.size(), "개")
                                        , fmKoreaMailTemplateService.getHtmlForSendMail(crawledArticles)
                                );
                                log.info("## Send Email: {}", crawledArticles.get(0).getKeyword());
                            });
                });

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
        List<FmKoreaArticleDto> articleList =
                fmKoreaCrawlingByPopularService.getFmKoreaCrawlingByPopularToStock(LocalTime.now(), 180L);

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
