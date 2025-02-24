package org.bbsk.mysender.fmkorea.service;

import lombok.extern.slf4j.Slf4j;
import org.bbsk.mysender.annotation.MeasureExecutionTime;
import org.bbsk.mysender.crawler.SeleniumUtils;
import org.bbsk.mysender.fmkorea.constant.FmKoreaStockEnum;
import org.bbsk.mysender.fmkorea.dto.FmKoreaMailDto;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.bbsk.mysender.fmkorea.dto.FmKoreaMailDto.*;

@Service
@Transactional(readOnly = true)
@Slf4j
public class FmKoreaService {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    /**
     * 에펨코리아 - 주식 게시판 - 키워드 검색
     *
     * @param chromeDriver
     * @param keyword
     * @return List<FmKoreaMailDto>
     */
    @MeasureExecutionTime
    public List<FmKoreaMailDto> getFmKoreaSearchKeywordByStock(WebDriver chromeDriver, String keyword) {
        // TODO 중복 걸러서 가져오기 방법 고민 (제일 마지막 게시글 캐시처리 후 해당 글 이후부터 크롤링)
        // TODO 크롤링 시간대 고민
        List<FmKoreaMailDto> dtoList = new ArrayList<>();
        try {
            // 부모 요소 가져오기
            WebElement parentElement = getParentElement(chromeDriver);

            // 부모 요소에서 게시글 리스트 가져오기
            List<WebElement> articles = getArticles(parentElement);

            for (int i = 0; i < articles.size(); i++) {
                // 현재 작업건수 출력
                // printProcessingCount(i + 1, articles.size());
                try {
                    // 페이지 변경 뒤로 가기 후 다시 요소 찾기
                    parentElement = getParentElement(chromeDriver);
                    // 작성 시간 요소 찾기
                    WebElement timeElement = getTimeElement(parentElement, i); // 해당 게시글의 작성 시간

                    // 두 시간 전 ~ 현재 시간 게시글 조회
                    if (!isValidTime(timeElement)) {
                        break;
                    }

                    articles = getArticles(parentElement);
                    WebElement article = articles.get(i);

                    // 현재 작업중인 제목과 기존에 생성된 객체랑 동일하면 해당 작업 건너뛰기
                    if (isDuplicatedArticle(dtoList, article, timeElement)) {
                        log.info("## 중복 게시글 발생 (신규 게시글 생성)");
                        continue;
                    }

                    // 메일 객체 생성
                    dtoList.add(getDtoBuilder(chromeDriver, keyword, getValue(article), getValue(timeElement), article.getAttribute("href"))
                            .build());

                    // local 환경에서는 2개만 조회
//                    if ("local".equals(activeProfile) && i == 1) {
//                        break;
//                    }

                    log.info("## {}번째 작업완료", i + 1);

                    // **목록 페이지로 다시 이동**
                    chromeDriver.navigate().back();
                } catch (Exception e) {
                    log.error("## 개별 게시글 크롤링 중 오류 발생, 다음 글로 이동: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("## 크롤링 과정에서 오류 발생: {}", e.getMessage());
        } finally {
            // WebDriver 종료
            chromeDriver.quit();
        }

        return dtoList;
    }

    private static boolean isDuplicatedArticle(List<FmKoreaMailDto> dtoList, WebElement article, WebElement timeElement) {
        return !dtoList.stream()
                .filter(dto -> dto.getTitle().equals(getValue(article)) && dto.getCreatedTime().equals(getValue(timeElement)))
                .toList()
                .isEmpty();
    }

    /**
     * @param chromeDriver
     * @param keyword 검색 키워드
     * @param title 제목
     * @param createdTime 작성시간
     * @param link 글 본문 링크
     * @return
     */
    private static FmKoreaMailDtoBuilder getDtoBuilder(WebDriver chromeDriver, String keyword, String title, String createdTime, String link) {
        // ## 제목, 작성시간, 링크, 키워드
        FmKoreaMailDtoBuilder entityBuilder = builder()
                        .title(title)
                        .createdTime(createdTime)
                        .link(link)
                        .keyword(keyword);

        // 해당 글 링크로 이동 후 내용 크롤링
        chromeDriver.get(link);

        // 글 본문 크롤링
        // ## 글 본문, 이미지 링크
        getContentCrawling(chromeDriver, entityBuilder);
        return entityBuilder;
    }

    private static boolean isValidTime(WebElement timeElement) {
        String[] hoursAndMinutes = getValue(timeElement).split(":");
        if(hoursAndMinutes.length != 2) {
            log.error("## 작성시간 에러 {}", getValue(timeElement));
            return false;
        }
        return (Integer.parseInt(getTwoHoursBefore()) <= Integer.parseInt(hoursAndMinutes[0]));
    }

    /**
     * 작성시간 조회
     * @param parentElement
     * @param i
     * @return
     */
    private static WebElement getTimeElement(WebElement parentElement, int i) {
        return parentElement.findElements(By.cssSelector("td.time")).get(i);
    }

    /**
     * 두 시간 전 조회
     * @return
     */
    private static String getTwoHoursBefore() {
        return LocalTime.now().minusHours(2).format(DateTimeFormatter.ofPattern("HH"));
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
    private static void getContentCrawling(WebDriver chromeDriver, FmKoreaMailDtoBuilder entityBuilder) {
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
            log.error("## 본문 내용 로드 실패: {}", ex.getMessage());
        }
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