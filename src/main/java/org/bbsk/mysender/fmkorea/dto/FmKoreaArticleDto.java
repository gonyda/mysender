package org.bbsk.mysender.fmkorea.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class FmKoreaArticleDto {
    private String keyword;
    private String title;
    private String createdTime;
    private String link;
    private String content;
    private List<String> imageUrlList = new ArrayList<>();
}