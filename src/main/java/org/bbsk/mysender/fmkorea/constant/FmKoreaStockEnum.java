package org.bbsk.mysender.fmkorea.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * 에펨코리아 - 주식게시판
 */
public enum FmKoreaStockEnum {

    // 도메인
    BASE_URL("https://www.fmkorea.com"),

    // 키워드 검색
    FIRST_URL("https://www.fmkorea.com/search.php?mid=stock&listStyle=list&search_keyword="),
    SECOND_URL("&search_target=title_content&page=1"),

    // 인기글
    POPULAR_URL("https://www.fmkorea.com/index.php?mid=stock&sort_index=pop&order_type=desc&page="),

    // 본문
    // SELECTOR
    SELECTOR_CONTENT("div.rd_body div.xe_content"),
    SELECTOR_POST_TIME("div.top_area span.date"),
    SELECTOR_NEXT_ARTICLE_BTN(".prev_next_btns .next a"),
    SELECTOR_TITLE("h1.np_18px > span.np_18px_span");

    private final String value;

    FmKoreaStockEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 키워드 검색 URL
     * @param keyword
     * @return
     */
    public static String getFullUrl(String keyword) {
        return StringUtils.join(FIRST_URL.getValue()
                                , keyword
                                , SECOND_URL.getValue());
    }
}
