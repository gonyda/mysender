package org.bbsk.mysender.fmkorea.service;

import org.bbsk.mysender.crawler.SeleniumUtils;
import org.bbsk.mysender.fmkorea.constant.FmKoreaStockEnum;
import org.bbsk.mysender.fmkorea.dto.ContentCrawlingDto;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.time.Duration;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class FmKoreaCrawlingByPopularService {

    private static final Logger log = LoggerFactory.getLogger(FmKoreaCrawlingByPopularService.class);

    private static final String CSS_SELECTOR_BY_POST_LIST = "div.fm_best_widget ul li";

    private final FmKoreaCrawlingService fmKoreaCrawlingService;

    public FmKoreaCrawlingByPopularService(FmKoreaCrawlingService fmKoreaCrawlingService) {
        this.fmKoreaCrawlingService = fmKoreaCrawlingService;
    }


    /**
     * 인기글 크롤링
     *
     * @param chromeDriver
     * @param now
     * @param crawlingTime 스케줄러 시간 -> 분 변환
     * @return
     */
    public List<FmKoreaArticleDto> getFmKoreaCrawlingByPopularToStock(WebDriver chromeDriver, LocalTime now, long crawlingTime) {
        AtomicInteger workCnt = new AtomicInteger();
        List<FmKoreaArticleDto> dtoList = getCrawlingToArticles(chromeDriver, crawlingTime, now)
                .stream()
                .map(link -> {
                    chromeDriver.get(link);
                    ContentCrawlingDto crawlingDto = fmKoreaCrawlingService.getContentCrawling(chromeDriver);
                    log.info("## Crawled {} posts", workCnt.incrementAndGet());
                    return crawlingDto.getFmKoreaArticleDto();
                })
                .collect(Collectors.toList());

        SeleniumUtils.close(chromeDriver);
        log.info("## End Crawling");

        return dtoList;
    }

    /**
     * 크롤링 시간 기준 크롤링 할 인기글 조회
     *
     * @param chromeDriver
     * @param crawlingTime
     * @param now
     * @return
     */
    private static List<String> getCrawlingToArticles(WebDriver chromeDriver, long crawlingTime, LocalTime now) {
        return getParentElement(chromeDriver)
                .findElements(By.cssSelector(CSS_SELECTOR_BY_POST_LIST))
                .stream()
                // takeWhile은 스트림을 순회하면서 조건이 false가 되는 순간 이후 요소들은 모두 무시
                .takeWhile(article -> !isOverByCrawlingTime(crawlingTime, now, article))
                .map(article -> article.findElement(By.cssSelector("h3.title a")).getAttribute("href"))
                .collect(Collectors.toList());
    }

    /**
     * 크롤링 시간보다 오래 된 게시글 여부 파악
     * @param crawlingTime 분
     * @param now 스케줄러 동작 시간
     * @param article 게시글
     * @return
     */
    private static boolean isOverByCrawlingTime(long crawlingTime, LocalTime now, WebElement article) {
        // 시간 가져오기
        String articleTime = article.findElement(By.cssSelector("div > div > span.regdate")).getText().trim();
        // 현재시간 기준 세시간 전 게시글인지
        Duration duration = Duration.between(LocalTime.parse(articleTime), now);
        return duration.toMinutes() > crawlingTime;
    }

    private static WebElement getParentElement(WebDriver chromeDriver) {
        return SeleniumUtils.getParentElement(FmKoreaStockEnum.POPULAR_URL.getValue(), FmKoreaStockEnum.POPULAR_CLASSNAME.getValue(), chromeDriver);
    }
}