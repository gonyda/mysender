package org.bbsk.mysender.fmkorea.service.keyword;

import com.microsoft.playwright.Page;
import org.bbsk.mysender.fmkorea.constant.FmKoreaStockEnum;
import org.bbsk.mysender.fmkorea.dto.ContentCrawlingDto;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.bbsk.mysender.fmkorea.service.FmKoreaCommonContentCrawlingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FmKoreaContentCrawlingByKeywordService {

    private final FmKoreaCommonContentCrawlingService fmKoreaCommonContentCrawlingService;

    public FmKoreaContentCrawlingByKeywordService(FmKoreaCommonContentCrawlingService fmKoreaCommonContentCrawlingService) {
        this.fmKoreaCommonContentCrawlingService = fmKoreaCommonContentCrawlingService;
    }

    /**
     * 키워드 검색 - 게시글 본문 크롤링
     * 작성시간, 타이틀, 본문, 이미지, 다음글 링크
     *
     * @param page
     * @param keyword
     * @param now
     * @param crawlingTime
     * @return
     */
    public ContentCrawlingDto getContentCrawling(Page page, String keyword, LocalDateTime now, int crawlingTime) {
        // 1. 작성 시간 크롤링
        String postingTime = fmKoreaCommonContentCrawlingService.getPostingTime(page);

        // 현재시간 기준 두시간 전 게시글 이면 크롤링 X (이미 이메일 발송 된 게시글)
        if(isBeforeTwoHoursAgo(now, postingTime, crawlingTime)) {
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

    public boolean isBeforeTwoHoursAgo(LocalDateTime now, String timeStr, int crawlingTime) {
        // 게시글 작성시간이 2시간 전보다 이전인지 비교
        return LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
                .isBefore(now.minusHours(crawlingTime));
    }
}
