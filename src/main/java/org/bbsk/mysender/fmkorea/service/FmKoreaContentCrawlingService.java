package org.bbsk.mysender.fmkorea.service;


import com.microsoft.playwright.Page;
import org.bbsk.mysender.fmkorea.constant.FmKoreaStockEnum;
import org.bbsk.mysender.fmkorea.dto.ContentCrawlingDto;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 본문 크롤링 서비스
 */
@Service
public class FmKoreaContentCrawlingService {

    private static final Logger log = LoggerFactory.getLogger(FmKoreaContentCrawlingService.class);

    private final FmKoreaCommonContentCrawlingService fmKoreaCommonContentCrawlingService;

    public FmKoreaContentCrawlingService(FmKoreaCommonContentCrawlingService fmKoreaCommonContentCrawlingService) {
        this.fmKoreaCommonContentCrawlingService = fmKoreaCommonContentCrawlingService;
    }

    /**
     * 인기글 크롤링
     *
     * @param page
     * @return
     */
    public ContentCrawlingDto getContentCrawling(Page page) {
        // 1. 작성 시간 크롤링
        String postingTime = fmKoreaCommonContentCrawlingService.getPostingTime(page);
        // 2. 제목 크롤링
        String title = fmKoreaCommonContentCrawlingService.getTitle(page);
        // 3. 본문 내용 크롤링 (HTML 포함)
        // 이미지, 동영상 등 template service 에서 처리
        String content = fmKoreaCommonContentCrawlingService.getContent(page);

        return ContentCrawlingDto.builder()
                .fmKoreaArticleDto(FmKoreaArticleDto.builder()
                        .link(page.url())
                        .title(title)
                        .content(content)
                        .postingTime(postingTime)
                        .build())
                .build();
    }



    /**
     * 키워드 검색 - 게시글 본문 크롤링
     * 작성시간, 타이틀, 본문, 이미지, 다음글 링크
     *
     * @param page
     * @param keyword
     * @param now
     * @return
     */
    public ContentCrawlingDto getContentCrawling(Page page, String keyword, LocalDateTime now, int crawlingTime) {
        // 1. 작성 시간 크롤링
        String postingTime = fmKoreaCommonContentCrawlingService.getPostingTime(page);

        // 현재시간 기준 두시간 전 게시글 이면 크롤링 X (이미 이메일 발송 된 게시글)
        if(fmKoreaCommonContentCrawlingService.isBeforeTwoHoursAgo(now, postingTime, crawlingTime)) {
            return ContentCrawlingDto.builder()
                    .isOverByTime(true)
                    .build();
        }

        // 2. 제목 크롤링
        String title = fmKoreaCommonContentCrawlingService.getTitle(page);

        // 3. 본문 내용 크롤링 (HTML 포함)
        // 이미지, 동영상 등 template service 에서 처리
        String content = fmKoreaCommonContentCrawlingService.getContent(page);

        // 4. 다음 글 링크 크롤링
        String nextPageUrl = fmKoreaCommonContentCrawlingService.getNextPageUrl(page);

        return ContentCrawlingDto.builder()
                .isOverByTime(false)
                .fmKoreaArticleDto(FmKoreaArticleDto.builder()
                        .keyword(keyword)
                        .link(page.url())
                        .title(title)
                        .content(content)
                        .postingTime(postingTime)
                        .build())
                .nextPageUrl(FmKoreaStockEnum.BASE_URL.getValue() + nextPageUrl)
                .build();
    }
}
