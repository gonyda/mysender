package org.bbsk.mysender.fmkorea.service.popular;


import com.microsoft.playwright.Page;
import org.bbsk.mysender.fmkorea.dto.ContentCrawlingDto;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.bbsk.mysender.fmkorea.service.FmKoreaCommonContentCrawlingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 본문 크롤링 서비스
 */
@Service
public class FmKoreaContentCrawlingByPopularService {

    private static final Logger log = LoggerFactory.getLogger(FmKoreaContentCrawlingByPopularService.class);

    private final FmKoreaCommonContentCrawlingService fmKoreaCommonContentCrawlingService;

    public FmKoreaContentCrawlingByPopularService(FmKoreaCommonContentCrawlingService fmKoreaCommonContentCrawlingService) {
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



}
