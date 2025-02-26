package org.bbsk.mysender.fmkorea.service;

import lombok.extern.slf4j.Slf4j;
import org.bbsk.mysender.annotation.MeasureExecutionTime;
import org.bbsk.mysender.crawler.SeleniumUtils;
import org.bbsk.mysender.fmkorea.constant.FmKoreaStockEnum;
import org.bbsk.mysender.fmkorea.dto.ContentCrawlingDto;
import org.bbsk.mysender.fmkorea.dto.FmKoreaMailDto;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class FmKoreaService {

    /**
     * 에펨코리아 - 주식 게시판 - 키워드 검색
     * 전체목록 - 글 본문 방문 (가장 최신글) - 다음글로 이동 (다음글 버튼)
     * 속도개선 (크롤링 횟수 줄이기)
     * @param chromeDriver
     * @param keyword
     * @return
     */
    @MeasureExecutionTime
    public List<FmKoreaMailDto> getFmKoreaCrawlingBySearchKeywordToStock(WebDriver chromeDriver, String keyword) {
        log.info("## Current Keyword: {}", keyword);

        // 전체 게시글 목록 크롤링
        WebElement parentElement = getParentElement(chromeDriver, keyword);
        // 게시글 목록 중 첫번째 글
        WebElement firstPost = parentElement.findElement(By.cssSelector("td.title.hotdeal_var8 a:not(.replyNum)"));
        // 첫 번째 게시글의 링크 가져오기
        String firstArticleLink = firstPost.getAttribute("href");
        // 해당 글 링크로 이동 후 내용 크롤링
        chromeDriver.get(firstArticleLink);

        List<FmKoreaMailDto> dtoList = new ArrayList<>();

        // 본문 글 크롤링
        int workCnt = 0;
        while (true) {
            ContentCrawlingDto crawlingDto = getContentCrawling(chromeDriver, keyword);

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

        return dtoList;
    }

    private static ContentCrawlingDto getContentCrawling(WebDriver chromeDriver, String keyword) {
        // 1. 작성 시간 크롤링
        WebElement dateElement = chromeDriver.findElement(By.cssSelector(".top_area .date"));
        String date = dateElement.getText();

        // 현재시간 기준 두시간 전 게시글 이면 크롤링 X (이미 이메일 발송 된 게시글)
        if(isBeforeTwoHoursAgo(date)) {
            return ContentCrawlingDto.builder()
                    .isDuplicated(true)
                    .build();
        }

        // 2. 제목 크롤링
        WebElement titleElement = chromeDriver.findElement(By.cssSelector(".top_area h1 .np_18px_span"));
        String title = getValue(titleElement);

        // 3. 본문 내용 크롤링
        WebElement contentElement = chromeDriver.findElement(By.cssSelector(".xe_content"));
        String content = getValue(contentElement);

        // 4. 이미지가 있는지 확인
        List<WebElement> images = contentElement.findElements(By.tagName("img"));
        List<String> imgUrlList = new ArrayList<>();
        if (!images.isEmpty()) {
            for (WebElement img : images) {
                // ## 이미지 url
                imgUrlList.add(img.getAttribute("src"));
            }
        }

        // 5. 다음 글 링크 크롤링
        WebElement nextPostElement = chromeDriver.findElement(By.cssSelector(".prev_next_btns .next a"));
        // '다음 글' 링크 가져오기
        String nextPageUrl = nextPostElement.getAttribute("href");

        return ContentCrawlingDto.builder()
                .isDuplicated(false)
                .mailDto(FmKoreaMailDto.builder()
                        .keyword(keyword)
                        .link(chromeDriver.getCurrentUrl())
                        .title(title)
                        .content(content)
                        .createdTime(date)
                        .imageUrlList(imgUrlList)
                        .build())
                .nextPageUrl(nextPageUrl)
                .build();
    }

    public static boolean isBeforeTwoHoursAgo(String timeStr) {
        // 입력된 시간 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

        // 입력된 문자열을 LocalDateTime으로 변환
        LocalDateTime inputTime = LocalDateTime.parse(timeStr, formatter);

        // 현재 시간 기준 2시간 전 계산
        LocalDateTime twoHoursAgo = LocalDateTime.now().minusHours(2);

        // 입력된 시간이 2시간 전보다 이전인지 비교
        return inputTime.isBefore(twoHoursAgo);
    }

    /**
     * 해당 element Text 출력
     * @param element
     * @return
     */
    private static String getValue(WebElement element) {
        return element.getText().trim();
    }

    private static WebElement getParentElement(WebDriver chromeDriver, String keyword) {
        return SeleniumUtils.getParentElement(FmKoreaStockEnum.getFullUrl(keyword), FmKoreaStockEnum.FIRST_CLASSNAME.getValue(), chromeDriver);
    }
}