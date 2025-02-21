package org.bbsk.mysender.fmkorea.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에펨코리아 - 주식게시판
 */
@Getter
@RequiredArgsConstructor
public enum FmKoreaStockEnum {

    PLTR_URL("https://www.fmkorea.com/search.php?mid=stock&listStyle=list&search_keyword=팔란티어&search_target=title_content&page=1"),
    FIRST_CLASSNAME("bd_lst");

    private final String value;
}
