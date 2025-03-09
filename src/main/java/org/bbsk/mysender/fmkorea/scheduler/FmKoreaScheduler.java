package org.bbsk.mysender.fmkorea.scheduler;

import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.bbsk.mysender.fmkorea.service.keyword.FmKoreaCrawlingByKeywordSearchService;
import org.bbsk.mysender.fmkorea.service.popular.FmKoreaCrawlingByPopularService;
import org.bbsk.mysender.fmkorea.service.FmKoreaKeywordService;
import org.bbsk.mysender.fmkorea.template.FmKoreaMailTemplateService;
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

@Component
@EnableScheduling
public class FmKoreaScheduler {

    private static final Logger log = LoggerFactory.getLogger(FmKoreaScheduler.class);

    private final FmKoreaCrawlingByKeywordSearchService fmKoreaCrawlingByKeywordSearchService;
    private final FmKoreaCrawlingByPopularService fmKoreaCrawlingByPopularService;
    private final GmailService gmailService;
    private final FmKoreaKeywordService fmKoreaKeywordService;
    private final FmKoreaMailTemplateService fmKoreaMailTemplateService;

    public FmKoreaScheduler(FmKoreaCrawlingByKeywordSearchService fmKoreaCrawlingByKeywordSearchService
            , FmKoreaCrawlingByPopularService fmKoreaCrawlingByPopularService
            , GmailService gmailService
            , FmKoreaKeywordService fmKoreaKeywordService
            , FmKoreaMailTemplateService fmKoreaMailTemplateService) {
        this.fmKoreaCrawlingByKeywordSearchService = fmKoreaCrawlingByKeywordSearchService;
        this.fmKoreaCrawlingByPopularService = fmKoreaCrawlingByPopularService;
        this.gmailService = gmailService;
        this.fmKoreaKeywordService = fmKoreaKeywordService;
        this.fmKoreaMailTemplateService = fmKoreaMailTemplateService;
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

        List<FmKoreaArticleDto> crawledArticles =
                fmKoreaCrawlingByPopularService.getFmKoreaCrawlingByPopularToStock(LocalTime.now(), 180L);

        if (!crawledArticles.isEmpty()) {
            sendEmail(crawledArticles);
        }

        log.info("## Popular End");
    }

    private void sendEmail(List<FmKoreaArticleDto> crawledArticles) {
        gmailService.sendEmail(
                GmailEnum.TO.getValue()
                , GmailEnum.TITLE.formatTitle(crawledArticles.get(0).getKeyword(), crawledArticles.size())
                , fmKoreaMailTemplateService.getHtmlForSendMail(crawledArticles)
        );

        log.info("## Send Email: {}", crawledArticles.get(0).getKeyword());
    }
}
