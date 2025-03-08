package org.bbsk.mysender.fmkorea.service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.bbsk.mysender.crawler.PlayWrightUtils;
import org.bbsk.mysender.fmkorea.constant.FmKoreaStockEnum;
import org.bbsk.mysender.fmkorea.dto.ContentCrawlingDto;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalTime;
import java.time.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class FmKoreaCrawlingByPopularService {

    private static final Logger log = LoggerFactory.getLogger(FmKoreaCrawlingByPopularService.class);

    private final FmKoreaCrawlingService fmKoreaCrawlingService;

    public FmKoreaCrawlingByPopularService(FmKoreaCrawlingService fmKoreaCrawlingService) {
        this.fmKoreaCrawlingService = fmKoreaCrawlingService;
    }


    /**
     * 인기글 크롤링
     *
     * @param browser
     * @param now
     * @param crawlingTime 스케줄러 시간 -> 분 변환
     * @return
     */
    public List<FmKoreaArticleDto> getFmKoreaCrawlingByPopularToStock(Browser browser, LocalTime now, long crawlingTime) {
        AtomicInteger workCnt = new AtomicInteger();

        List<FmKoreaArticleDto> dtoList = getCrawlingToArticles(browser, crawlingTime, now)
                .stream()
                .map(link -> {
                    try (Page page = PlayWrightUtils.getBrowser().newPage()) {
                        page.navigate(link);
                        ContentCrawlingDto crawlingDto = fmKoreaCrawlingService.getContentCrawling(page);
                        log.info("## Crawled {} posts", workCnt.incrementAndGet());
                        return crawlingDto.getFmKoreaArticleDto();
                    }
                })
                .toList();

        log.info("## End Crawling");

        return dtoList;
    }

    /**
     * 크롤링 시간 기준 크롤링 할 인기글 조회
     *
     * @param browser
     * @param crawlingTime
     * @param now
     * @return
     */
    private static List<String> getCrawlingToArticles(Browser browser, long crawlingTime, LocalTime now) {
        Locator parentElement = getParentElement(browser);

        // 게시글 리스트 항목들 추출
        List<Locator> articles = parentElement.locator("ul > li.li").all();

        List<String> result = new ArrayList<>();
        for (Locator article : articles) {
            if (isOverByCrawlingTime(crawlingTime, now, article)) {
                break;
            }

            String href = article.locator("h3.title > a").getAttribute("href");
            if (href != null) {
                result.add(parentElement.page().url() + href);
            }
        }


        return result;
    }

    /**
     * 크롤링 시간보다 오래 된 게시글 여부 파악
     * @param crawlingTime 분
     * @param now 스케줄러 동작 시간
     * @param article 게시글
     * @return
     */
    private static boolean isOverByCrawlingTime(long crawlingTime, LocalTime now, Locator article) {
        // 시간 가져오기
        String articleTime = article.locator("span.regdate").innerText().trim();
        // 현재시간 기준 세시간 전 게시글인지
        Duration duration = Duration.between(LocalTime.parse(articleTime), now);
        return duration.toMinutes() > crawlingTime;
    }

    private static Locator getParentElement(Browser browser) {
        Page page = browser.newPage();
        page.navigate(FmKoreaStockEnum.POPULAR_URL.getValue());
        return page.locator(FmKoreaStockEnum.POPULAR_CLASSNAME.getValue());
    }
}