package org.bbsk.mysender.fmkorea.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 에펨코리아 - 주식게시판
 */
@Getter
@RequiredArgsConstructor
public enum FmKoreaStockEnum {

    // 키워드 검색
    PREV_URL("https://www.fmkorea.com/search.php?mid=stock&listStyle=list&search_keyword="),
    NEXT_URL("&search_target=title_content&page=1"),
    FIRST_CLASSNAME("bd_lst"),

    // 인기글
    POPULAR_URL("https://www.fmkorea.com/index.php?mid=stock&sort_index=pop&order_type=desc"),
    POPULAR_CLASSNAME("fm_best_widget");

    private final String value;

    public static String getFullUrl(String keyword) {
        return StringUtils.join(PREV_URL.getValue()
                                , keyword
                                , NEXT_URL.getValue());
    }
}
