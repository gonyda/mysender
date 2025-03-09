package org.bbsk.mysender.fmkorea.service;

import com.microsoft.playwright.Page;
import org.bbsk.mysender.fmkorea.constant.FmKoreaStockEnum;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FmKoreaCommonContentCrawlingService {

    public String getPostingTime(Page page) {
        return page.waitForSelector(FmKoreaStockEnum.SELECTOR_POST_TIME.getValue()).innerText();
    }

    public String getTitle(Page page) {
        return page.waitForSelector(FmKoreaStockEnum.SELECTOR_TITLE.getValue()).innerHTML().trim();
    }

    public String getContent(Page page) {
        return page.waitForSelector(FmKoreaStockEnum.SELECTOR_CONTENT.getValue()).innerHTML().trim();
    }

    public String getNextPageUrl(Page page) {
        return page.waitForSelector(".prev_next_btns .next a").getAttribute("href");
    }

    public boolean isBeforeTwoHoursAgo(LocalDateTime now, String timeStr, int crawlingTime) {
        // 게시글 작성시간이 2시간 전보다 이전인지 비교
        return LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
                .isBefore(now.minusHours(crawlingTime));
    }
}
