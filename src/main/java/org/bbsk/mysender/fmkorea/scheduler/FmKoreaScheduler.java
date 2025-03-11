package org.bbsk.mysender.fmkorea.scheduler;

import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.bbsk.mysender.fmkorea.service.keyword.FmKoreaCrawlingByKeywordSearchService;
import org.bbsk.mysender.fmkorea.service.popular.FmKoreaCrawlingByPopularService;
import org.bbsk.mysender.fmkorea.service.FmKoreaKeywordService;
import org.bbsk.mysender.fmkorea.template.FmKoreaMailTemplateV2Service;
import org.bbsk.mysender.gmail.constant.GmailEnum;
import org.bbsk.mysender.gmail.service.GmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Component
@EnableScheduling
public class FmKoreaScheduler {

    private static final Logger log = LoggerFactory.getLogger(FmKoreaScheduler.class);

    private final FmKoreaCrawlingByKeywordSearchService fmKoreaCrawlingByKeywordSearchService;
    private final FmKoreaCrawlingByPopularService fmKoreaCrawlingByPopularService;
    private final GmailService gmailService;
    private final FmKoreaKeywordService fmKoreaKeywordService;
    private final FmKoreaMailTemplateV2Service fmKoreaMailTemplateV2Service;

    public FmKoreaScheduler(FmKoreaCrawlingByKeywordSearchService fmKoreaCrawlingByKeywordSearchService
            , FmKoreaCrawlingByPopularService fmKoreaCrawlingByPopularService
            , GmailService gmailService
            , FmKoreaKeywordService fmKoreaKeywordService
            , FmKoreaMailTemplateV2Service fmKoreaMailTemplateV2Service) {
        this.fmKoreaCrawlingByKeywordSearchService = fmKoreaCrawlingByKeywordSearchService;
        this.fmKoreaCrawlingByPopularService = fmKoreaCrawlingByPopularService;
        this.gmailService = gmailService;
        this.fmKoreaKeywordService = fmKoreaKeywordService;
        this.fmKoreaMailTemplateV2Service = fmKoreaMailTemplateV2Service;
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

        keywordList.stream()
                        .map(keyword -> fmKoreaCrawlingByKeywordSearchService
                                .getFmKoreaCrawlingBySearchKeywordToStock(keyword, LocalDateTime.now(), 2))
                        .filter(list -> !list.isEmpty())
                        .forEach(this::sendEmail);

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

        Optional.of(fmKoreaCrawlingByPopularService.getFmKoreaCrawlingByPopularToStock(LocalTime.now(), 180L))
                .filter(list -> !list.isEmpty())
                .ifPresent(this::sendEmail);

        log.info("## Popular End");
    }

    private void sendEmail(List<FmKoreaArticleDto> crawledArticles) {
        gmailService.sendEmail(
                GmailEnum.TO.getValue()
                , GmailEnum.TITLE.formatTitle(crawledArticles.get(0).getKeyword(), crawledArticles.size())
                , fmKoreaMailTemplateV2Service.getHtmlForMail(crawledArticles)
        );

        log.info("## Send Email: {}", crawledArticles.get(0).getKeyword());
    }
}
