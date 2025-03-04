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
public class FmKoreaCrawlingByPopularService {

    private final FmKoreaCrawlingService fmKoreaCrawlingService;

    public static final String CSS_SELECTOR_BY_FIRST_POST = ".fm_best_widget ul li:first-child h3.title a";


    public List<FmKoreaArticleDto> getFmKoreaCrawlingByPopularToStock(WebDriver chromeDriver, LocalDateTime now, int crawlingTime) {
        // 첫번째 게시글 본문으로 이동
        moveArticle(chromeDriver);

        // 본문 글 크롤링
        int workCnt = 0;
        List<FmKoreaArticleDto> dtoList = new ArrayList<>();
        while (true) {
            ContentCrawlingDto crawlingDto = fmKoreaCrawlingService.getContentCrawling(chromeDriver, "", now, crawlingTime);

            // 현재시간 기준 두시간 전 게시글 이면 크롤링 X (이미 이메일 발송 된 게시글)
            if(crawlingDto.isDuplicated()) {
                break;
            }

            dtoList.add(crawlingDto.mailDto());
            workCnt++;
            log.info("## Crawled {} posts", workCnt);

            // 다음 글 이동
            chromeDriver.get(crawlingDto.nextPageUrl());
        }

        SeleniumUtils.close(chromeDriver);

        log.info("## End Crawling");

        return dtoList;
    }

    /**
     * 게시글 본문으로 이동
     * @param chromeDriver
     */
    private static void moveArticle(WebDriver chromeDriver) {
        // 첫번째 게시글 본문 링크 조회
        // 해당 글 링크로 이동
        chromeDriver.get(getFirstArticleLink(chromeDriver));
    }

    /**
     * 키워드로 검색한 게시글 중 첫번째 게시글 링크 조회
     * @param chromeDriver
     * @return
     */
    private static String getFirstArticleLink(WebDriver chromeDriver) {
        // 전체 게시글 목록 크롤링
        // 게시글 목록 중 첫번째 글
        WebElement firstPost = getParentElement(chromeDriver)
                .findElement(By.cssSelector(CSS_SELECTOR_BY_FIRST_POST));
        // 첫 번째 게시글의 링크 가져오기
        return firstPost.getAttribute("href");
    }

    private static WebElement getParentElement(WebDriver chromeDriver) {
        return SeleniumUtils.getParentElement(FmKoreaStockEnum.POPULAR_URL.getValue(), FmKoreaStockEnum.POPULAR_CLASSNAME.getValue(), chromeDriver);
    }
}