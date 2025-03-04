package org.bbsk.mysender.fmkorea.dto;

import lombok.*;

@Builder
public record ContentCrawlingDto(boolean isDuplicated, FmKoreaArticleDto mailDto, String nextPageUrl) {}
