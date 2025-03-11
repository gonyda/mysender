package org.bbsk.mysender.fmkorea.service.popular;

import com.microsoft.playwright.*;
import org.bbsk.mysender.crawler.PlayWrightUtils;
import org.bbsk.mysender.fmkorea.constant.FmKoreaStockEnum;
import org.bbsk.mysender.fmkorea.dto.ContentCrawlingDto;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FmKoreaCrawlingByPopularService {

    public static final String SELECTOR_ARTICLE_LIST = "div.fm_best_widget._bd_pc ul li";
    public static final String SELECTOR_ARTICLE_LINK = "h3.title a";
    public static final String SELECTOR_ARTICLE_POSTING_TIME = "span.regdate";

    private static final Logger log = LoggerFactory.getLogger(FmKoreaCrawlingByPopularService.class);

    private final FmKoreaContentCrawlingByPopularService fmKoreaContentCrawlingByPopularService;

    public FmKoreaCrawlingByPopularService(FmKoreaContentCrawlingByPopularService fmKoreaContentCrawlingByPopularService) {
        this.fmKoreaContentCrawlingByPopularService = fmKoreaContentCrawlingByPopularService;
    }

    /**
     * 인기글 크롤링
     *
     * @param now
     * @param crawlingTime 스케줄러 시간 -> 분 변환
     * @return
     */
    public List<FmKoreaArticleDto> getFmKoreaCrawlingByPopularToStock(LocalTime now, long crawlingTime) {
        // 메인 페이지 생성 및 인기 게시글 목록 크롤링
        BrowserContext browserContext = PlayWrightUtils.getBrowser();
        Page mainPage = browserContext.newPage();

        // 인기글 리스트 조회 - 링크, 작성시간
        List<String> linkList = getArticleLinkList(now, crawlingTime, mainPage);

        // 본문 크롤링
        List<FmKoreaArticleDto> crawledArticles = getCrawlingArticles(linkList, mainPage);

        // 전체 크롤링 작업 종료 후 브라우저, 컨텍스트 닫기
        PlayWrightUtils.close(browserContext, mainPage);

        return crawledArticles;
    }

    /**
     * 크롤링 대상
     *
     * @param now
     * @param crawlingTime
     * @param mainPage
     * @return
     */
    private static List<String> getArticleLinkList(LocalTime now, long crawlingTime, Page mainPage) {
        // 페이지 번호
        AtomicInteger pageNo = new AtomicInteger();
        List<String> linkList = new ArrayList<>();

        while (true) {
            // 크롤링 URL 이동
            mainPage.navigate(FmKoreaStockEnum.POPULAR_URL.getValue() + pageNo.incrementAndGet());

            // 대상 요소가 로드될 때까지 대기
            mainPage.waitForSelector(SELECTOR_ARTICLE_LIST);

            // li 태그들을 모두 선택하여 링크와 작성시간 추출
            List<ElementHandle> articleList = mainPage.querySelectorAll(SELECTOR_ARTICLE_LIST);

            // 크롤링 시간 범위 내의 게시글 링크 조회
            List<String> validLinks = articleList.stream()
                    .takeWhile(li -> !isOverByCrawlingTime(crawlingTime, now, getPostingTime(li)))
                    .map(FmKoreaCrawlingByPopularService::getLink)
                    .toList();

            linkList.addAll(validLinks);

            // 만약 페이지 내 모든 게시글이 크롤링 시간 범위 내의 게시글이라면 다음 페이지로 이동
            if (validLinks.size() < articleList.size()) {
                break;
            }
        }

        log.info("## 인기글 Target Page Size: {}", linkList.size());
        return linkList;
    }

    /**
     * 본문 크롤링 데이터
     *
     * @param linkList
     * @param mainPage
     * @return
     */
    private List<FmKoreaArticleDto> getCrawlingArticles(List<String> linkList, Page mainPage) {
        AtomicInteger workCnt = new AtomicInteger();
        return linkList.stream()
                .map(link -> {
                    mainPage.navigate(FmKoreaStockEnum.BASE_URL.getValue() + link);
                    ContentCrawlingDto crawlingDto = fmKoreaContentCrawlingByPopularService.getContentCrawling(mainPage);
                    log.info("## 인기글 Crawled {} posts", workCnt.incrementAndGet());
                    return crawlingDto.getFmKoreaArticleDto();
                }).toList();
    }

    /**
     * 크롤링 시간보다 오래 된 게시글 여부 파악
     * @param crawlingTime 분
     * @param now 스케줄러 동작 시간
     * @param postingTime 글 작성시간
     * @return
     */
    private static boolean isOverByCrawlingTime(long crawlingTime, LocalTime now, String postingTime) {
        // 현재시간 기준 세시간 전 게시글인지
        return Duration.between(LocalTime.parse(postingTime), now)
                .toMinutes() > crawlingTime;
    }

    /**
     * 게시글 링크 조회
     * @param li
     * @return
     */
    private static String getLink(ElementHandle li) {
        // 게시글 본문 링크 추출
        return li.querySelector(SELECTOR_ARTICLE_LINK).getAttribute("href");
    }

    /**
     * 게시글 작성시간 조회
     * @param li
     * @return
     */
    private static String getPostingTime(ElementHandle li) {
        // 작성시간 추출: span.regdate의 innerText
        return li.querySelector(SELECTOR_ARTICLE_POSTING_TIME).innerText().trim();
    }
}