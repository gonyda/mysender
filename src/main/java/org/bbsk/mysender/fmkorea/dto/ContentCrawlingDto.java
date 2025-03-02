package org.bbsk.mysender.fmkorea.dto;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ContentCrawlingDto {

    private boolean isDuplicated;
    private FmKoreaArticleDto mailDto;
    private String nextPageUrl;
}
