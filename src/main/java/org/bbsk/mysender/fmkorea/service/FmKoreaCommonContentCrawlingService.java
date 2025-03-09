package org.bbsk.mysender.fmkorea.service;

import com.microsoft.playwright.Page;
import org.bbsk.mysender.fmkorea.constant.FmKoreaStockEnum;
import org.springframework.stereotype.Service;


/**
 * fm korea 본문 크롤링
 */
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
}
