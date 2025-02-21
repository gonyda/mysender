package org.bbsk.mysender.fmkorea.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bbsk.mysender.crawler.SeleniumUtils;
import org.bbsk.mysender.fmkorea.constant.FmKoreaStockEnum;
import org.bbsk.mysender.gmail.service.GmailService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.bbsk.mysender.fmkorea.entity.FmKoreaSearchKeywordEntity.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FmKoreaService {

    private final GmailService gmailService;

    /**
     * 에펨코리아 - 주식 게시판 - 키워드 검색
     * @param chromeDriver
     */
    public void getFmKoreaSearchKeywordByStock(WebDriver chromeDriver) {
        log.info("## Start");

        Instant startTime = Instant.now();

        try {
            // 부모 요소 가져오기
            WebElement parentElement = getParentElement(chromeDriver);

            // 부모 요소에서 게시글 리스트 가져오기
            List<WebElement> articles = getArticles(parentElement);

            List<FmKoreaSearchKeywordEntityBuilder> entityList = new ArrayList<>();

            for (int i = 0; i < articles.size(); i++) {
                printProcessingCount(i + 1, articles.size());

                FmKoreaSearchKeywordEntityBuilder entityBuilder = builder();
                try {
                    // 페이지 변경 뒤로 가기 후 다시 요소 찾기
                    parentElement = getParentElement(chromeDriver);
                    articles = getArticles(parentElement);
                    List<WebElement> times = parentElement.findElements(By.cssSelector("td.time")); // 작성 시간 요소 찾기

                    WebElement article = articles.get(i);
                    WebElement timeElement = times.get(i); // 해당 게시글의 작성 시간

                    // 글 링크
                    String fullUrl = article.getAttribute("href");

                    // ## 제목, 작성시간, 링크
                    entityBuilder = entityBuilder
                                    .title(getValue(article))
                                    .createdTime(getValue(timeElement))
                                    .link(fullUrl);

                    // 해당 글 링크로 이동 후 내용 크롤링
                    chromeDriver.get(fullUrl);

                    // 글 본문 크롤링
                    getContentCrawling(chromeDriver, entityBuilder);

                    // **목록 페이지로 다시 이동**
                    chromeDriver.navigate().back();
                } catch (Exception e) {
                    log.error("개별 게시글 크롤링 중 오류 발생, 다음 글로 이동");
                    log.error(e.getMessage());
                }
                entityList.add(entityBuilder);
            }

            // TODO 이메일 발송
//            gmailService.sendHtmlEmail("bbsk3939@gmail.com", "연습입니다앙~", "발송되냐?");

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            // 작업 소요시간 출력
            printWorkDuration(startTime);

            log.info("## End");
            // WebDriver 종료
            chromeDriver.quit();
        }
    }

    /**
     * 현재 작업 진행 현황 출력
     * @param currentCnt 현재 작업 건수
     * @param totalCnt 총 작업 건수
     */
    private static void printProcessingCount(int currentCnt, int totalCnt) {
        log.info("## 현재 작업 중 ... {} / {}", currentCnt, totalCnt);
    }

    /**
     * 글의 본문 크롤링
     * @param chromeDriver
     * @param entityBuilder 메일로 발송 보낼 객체
     */
    private static void getContentCrawling(WebDriver chromeDriver, FmKoreaSearchKeywordEntityBuilder entityBuilder) {
        try {
            // **게시글 본문 크롤링**
            WebElement contentElement = chromeDriver.findElement(By.cssSelector("div.xe_content"));

            // ## 글 내용
            entityBuilder.content(getValue(contentElement));

            // 이미지가 있는지 확인
            List<WebElement> images = contentElement.findElements(By.tagName("img"));
            if (!images.isEmpty()) {
                List<String> imgUrlList = new ArrayList<>();
                for (WebElement img : images) {
                    imgUrlList.add(img.getAttribute("src"));
                }
                // ## 이미지 url
                entityBuilder.imageUrlList(imgUrlList);
            }
        } catch (Exception ex) {
            log.error("본문 내용 로드 실패: {}", ex.getMessage());
        }
    }

    /**
     * 총 소요시간 출력
     * @param startTime
     */
    private static void printWorkDuration(Instant startTime) {
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        log.info("## 총 소요시간: {}초", duration.toSeconds());
    }

    /**
     * 해당 element Text 출력
     * @param element
     * @return
     */
    private static String getValue(WebElement element) {
        return element.getText().trim();
    }

    /**
     * 게시글 목록 조회 (댓글 갯수 제외)
     * @param parentElement
     * @return
     */
    private static List<WebElement> getArticles(WebElement parentElement) {
        return parentElement.findElements(By.cssSelector("td.title.hotdeal_var8 a:not(.replyNum)")); // 댓글 링크 제외
    }

    /**
     * 부모 element 조회
     * @param chromeDriver
     * @return
     */
    private static WebElement getParentElement(WebDriver chromeDriver) {
        return SeleniumUtils.getParentElement(FmKoreaStockEnum.PLTR_URL.getValue(), FmKoreaStockEnum.FIRST_CLASSNAME.getValue(), chromeDriver);
    }
}