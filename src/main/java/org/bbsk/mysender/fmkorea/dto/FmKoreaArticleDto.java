package org.bbsk.mysender.fmkorea.dto;

import lombok.*;

import java.util.List;

@Builder(toBuilder = true)
public record FmKoreaArticleDto(String keyword, String title, String createdTime, String link, String content, List<String> imageUrlList) {}