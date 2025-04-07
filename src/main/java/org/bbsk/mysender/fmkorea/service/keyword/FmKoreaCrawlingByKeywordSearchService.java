package org.bbsk.mysender.fmkorea.service.keyword;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.bbsk.mysender.crawler.PlayWrightUtils;
import org.bbsk.mysender.fmkorea.constant.FmKoreaStockEnum;
import org.bbsk.mysender.fmkorea.dto.ContentCrawlingDto;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FmKoreaCrawlingByKeywordSearchService {

    private static final Logger log = LoggerFactory.getLogger(FmKoreaCrawlingByKeywordSearchService.class);
    public static final String SELECTOR_FIRST_ARTICLE = "tbody > tr:not(.notice) td.title a.hx";

    private final FmKoreaContentCrawlingByKeywordService fmKoreaContentCrawlingByKeywordService;

    public FmKoreaCrawlingByKeywordSearchService(FmKoreaContentCrawlingByKeywordService fmKoreaContentCrawlingByKeywordService) {
        this.fmKoreaContentCrawlingByKeywordService = fmKoreaContentCrawlingByKeywordService;
    }

    /**
     * 에펨코리아 - 주식 게시판 - 키워드 검색
     * 전체목록 - 글 본문 방문 (가장 최신글) - 다음글로 이동 (다음글 버튼)
     * 속도개선 (크롤링 횟수 줄이기)
     *
     * @param keyword
     * @param now
     * @param crawlingTime 시간
     * @return
     */
    public List<FmKoreaArticleDto> getFmKoreaCrawlingBySearchKeywordToStock(String keyword, LocalDateTime now, int crawlingTime) {
        log.info("## Current Keyword: {}", keyword);
        AtomicInteger workCnt = new AtomicInteger();

        BrowserContext browserContext = PlayWrightUtils.getBrowser();
        Page page = browserContext.newPage();
        // 첫번째 게시글 본문으로 이동
        moveFirstArticle(page, keyword);

        // 본문 글 크롤링
        List<FmKoreaArticleDto> dtoList = new ArrayList<>();
        while (true) {
            ContentCrawlingDto crawlingDto = fmKoreaContentCrawlingByKeywordService.getContentCrawling(page, keyword, now, crawlingTime);

            // 현재시간 기준 두시간 전 게시글 이면 크롤링 X (이미 이메일 발송 된 게시글)
            if(crawlingDto.isOverByTime()) {
                break;
            }

            dtoList.add(crawlingDto.getFmKoreaArticleDto());
            log.info("## {} Crawled {} posts", keyword, workCnt.incrementAndGet());

            // 다음 글 이동
            page.navigate(crawlingDto.getNextPageUrl());
        }

        PlayWrightUtils.close(browserContext, page);
        log.info("## End Crawling");

        Collections.reverse(dtoList);
        return dtoList;
    }

    /**
     * 게시글 본문으로 이동
     * @param page
     * @param keyword
     */
    private static void moveFirstArticle(Page page, String keyword) {
        // 첫번째 게시글 본문 이동
        String firstArticleLink = getFirstArticleLink(page, keyword);
        page.navigate(FmKoreaStockEnum.BASE_URL.getValue() + firstArticleLink);
    }

    /**
     * 키워드로 검색한 게시글 중 첫번째 게시글 링크 조회
     * @param page
     * @param keyword
     * @return
     */
    private static String getFirstArticleLink(Page page, String keyword) {
        // 크롤링 URL 이동
        page.navigate(FmKoreaStockEnum.getFullUrl(keyword));
        // 첫 게시글 링크 조회
        return page.waitForSelector(SELECTOR_FIRST_ARTICLE).getAttribute("href");
    }

}