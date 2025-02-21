package org.bbsk.mysender.fmkorea.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class FmKoreaSearchKeywordEntity {
    private String title;
    private String createdTime;
    private String link;
    private String content;
    private List<String> imageUrlList = new ArrayList<>();
}