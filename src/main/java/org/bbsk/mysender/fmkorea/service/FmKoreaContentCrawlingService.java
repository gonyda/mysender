package org.bbsk.mysender.fmkorea.service;


import com.microsoft.playwright.Page;
import org.bbsk.mysender.fmkorea.dto.ContentCrawlingDto;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 본문 크롤링 서비스
 */
@Service
public class FmKoreaContentCrawlingService {

    private static final Logger log = LoggerFactory.getLogger(FmKoreaContentCrawlingService.class);

    private static final String CSS_SELECTOR_BY_TITLE = ".top_area h1 .np_18px_span";
    private static final String CSS_SELECTOR_BY_CONTENT = ".xe_content";
    private static final String CSS_SELECTOR_BY_CREATED_TIME = ".top_area .date";
    private static final String CSS_SELECTOR_BY_NEXT_POST = ".prev_next_btns .next a";

    /**
     * 인기글 크롤링
     *
     * @param page
     * @return
     */
    public ContentCrawlingDto getContentCrawling(Page page) {
        // 1. 작성 시간 크롤링
        String createdTime = page.waitForSelector("div.top_area span.date").innerText();
        // 2. 제목 크롤링
        String title = page.waitForSelector("h1.np_18px > span.np_18px_span").innerText();
        // 3. 본문 내용 크롤링 (HTML 포함)
        // 이미지, 동영상 등 template service 에서 처리
        String content = page.waitForSelector("div.rd_body div.xe_content").innerHTML().trim();

        return ContentCrawlingDto.builder()
                .fmKoreaArticleDto(FmKoreaArticleDto.builder()
                        .link(page.url())
                        .title(title)
                        .content(content)
                        .createdTime(createdTime)
                        .build())
                .build();
    }

    /**
     * 키워드 검색 - 게시글 본문 크롤링
     * 작성시간, 타이틀, 본문, 이미지, 다음글 링크
     *
     * @param chromeDriver
     * @param keyword
     * @param now
     * @return
     */
    public ContentCrawlingDto getContentCrawling(WebDriver chromeDriver, String keyword, LocalDateTime now, int crawlingTime) {
        // 1. 작성 시간 크롤링
        String createdTime = getCratedTime(chromeDriver.findElement(By.cssSelector(CSS_SELECTOR_BY_CREATED_TIME)));

        // 현재시간 기준 두시간 전 게시글 이면 크롤링 X (이미 이메일 발송 된 게시글)
        if(isBeforeTwoHoursAgo(now, createdTime, crawlingTime)) {
            return ContentCrawlingDto.builder()
                    .isDuplicated(true)
                    .build();
        }

        // 2. 제목 크롤링
        String title = getTitle(chromeDriver.findElement(By.cssSelector(CSS_SELECTOR_BY_TITLE)));

        // 3. 본문 내용 크롤링
        WebElement contentElement =  chromeDriver.findElement(By.cssSelector(CSS_SELECTOR_BY_CONTENT));
        String content = getContent(contentElement);

        // 4. 이미지가 있는지 확인
        List<String> imgUrlList = getImgUrlList(contentElement);

        // 5. 다음 글 링크 크롤링
        String nextPageUrl = getNextPageUrl(chromeDriver.findElement(By.cssSelector(CSS_SELECTOR_BY_NEXT_POST)));

        return ContentCrawlingDto.builder()
                .isDuplicated(false)
                .fmKoreaArticleDto(FmKoreaArticleDto.builder()
                        .keyword(keyword)
                        .link(chromeDriver.getCurrentUrl())
                        .title(title)
                        .content(content)
                        .createdTime(createdTime)
                        .imageUrlList(imgUrlList)
                        .build())
                .nextPageUrl(nextPageUrl)
                .build();
    }

    private static String getTitle(WebElement titleElement) {
        return getText(titleElement);
    }

    private static String getContent(WebElement contentElement) {
        return getText(contentElement);
    }

    private static String getCratedTime(WebElement dateElement) {
        return dateElement.getText();
    }

    private static List<String> getImgUrlList(WebElement contentElement) {
        return contentElement.findElements(By.tagName("img"))
                .stream()
                .map(webElement -> webElement.getAttribute("src"))
                .toList();
    }

    private static String getNextPageUrl(WebElement nextPostElement) {
        return nextPostElement.getAttribute("href");
    }

    private static String getText(WebElement element) {
        return element.getText().trim();
    }

    private static boolean isBeforeTwoHoursAgo(LocalDateTime now, String timeStr, int crawlingTime) {
        // 게시글 작성시간이 2시간 전보다 이전인지 비교
        return LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
                .isBefore(now.minusHours(crawlingTime));
    }


}
