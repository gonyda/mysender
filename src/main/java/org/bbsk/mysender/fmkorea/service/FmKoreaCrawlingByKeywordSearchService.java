package org.bbsk.mysender.fmkorea.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bbsk.mysender.crawler.SeleniumUtils;
import org.bbsk.mysender.fmkorea.constant.FmKoreaStockEnum;
import org.bbsk.mysender.fmkorea.dto.ContentCrawlingDto;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FmKoreaCrawlingByKeywordSearchService {

    private final FmKoreaCrawlingService fmKoreaCrawlingService;

    public static final String CSS_SELECTOR_BY_FIRST_POST = "td.title.hotdeal_var8 a:not(.replyNum)";

    /**
     * 에펨코리아 - 주식 게시판 - 키워드 검색
     * 전체목록 - 글 본문 방문 (가장 최신글) - 다음글로 이동 (다음글 버튼)
     * 속도개선 (크롤링 횟수 줄이기)
     *
     * @param chromeDriver
     * @param keyword
     * @param now
     * @return
     */
    public List<FmKoreaArticleDto> getFmKoreaCrawlingBySearchKeywordToStock
        (WebDriver chromeDriver, String keyword, LocalDateTime now, int crawlingTime) {
        log.info("## Current Keyword: {}", keyword);

        // 첫번째 게시글 본문으로 이동
        moveArticle(chromeDriver, keyword);

        // 본문 글 크롤링
        int workCnt = 0;
        List<FmKoreaArticleDto> dtoList = new ArrayList<>();
        while (true) {
            ContentCrawlingDto crawlingDto = fmKoreaCrawlingService.getContentCrawling(chromeDriver, keyword, now, crawlingTime);

            // 현재시간 기준 두시간 전 게시글 이면 크롤링 X (이미 이메일 발송 된 게시글)
            if(crawlingDto.isDuplicated()) {
                break;
            }

            dtoList.add(crawlingDto.getMailDto());
            workCnt++;
            log.info("## Crawled {} posts", workCnt);

            // 다음 글 이동
            chromeDriver.get(crawlingDto.getNextPageUrl());
        }

        SeleniumUtils.close(chromeDriver);

        log.info("## End Crawling");

        return dtoList;
    }

    /**
     * 게시글 본문으로 이동
     * @param chromeDriver
     * @param keyword
     */
    private static void moveArticle(WebDriver chromeDriver, String keyword) {
        // 첫번째 게시글 본문 링크 조회
        // 해당 글 링크로 이동
        chromeDriver.get(getFirstArticleLink(chromeDriver, keyword));
    }

    /**
     * 키워드로 검색한 게시글 중 첫번째 게시글 링크 조회
     * @param chromeDriver
     * @param keyword
     * @return
     */
    private static String getFirstArticleLink(WebDriver chromeDriver, String keyword) {
        // 전체 게시글 목록 크롤링
        // 게시글 목록 중 첫번째 글
        WebElement firstPost = getParentElement(chromeDriver, keyword)
                .findElement(By.cssSelector(CSS_SELECTOR_BY_FIRST_POST));
        // 첫 번째 게시글의 링크 가져오기
        return firstPost.getAttribute("href");
    }

    /**
     * 주식게시판 (키워드 검색)
     * @param chromeDriver
     * @param keyword
     * @return
     */
    private static WebElement getParentElement(WebDriver chromeDriver, String keyword) {
        return SeleniumUtils.getParentElement(FmKoreaStockEnum.getFullUrl(keyword), FmKoreaStockEnum.FIRST_CLASSNAME.getValue(), chromeDriver);
    }
}