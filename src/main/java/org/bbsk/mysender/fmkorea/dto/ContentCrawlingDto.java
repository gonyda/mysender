package org.bbsk.mysender.fmkorea.dto;

public class ContentCrawlingDto {
    private final boolean isDuplicated;
    private final FmKoreaArticleDto fmKoreaArticleDto;
    private final String nextPageUrl;

    public boolean isDuplicated() {
        return isDuplicated;
    }

    public FmKoreaArticleDto getFmKoreaArticleDto() {
        return fmKoreaArticleDto;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    private ContentCrawlingDto(Builder builder) {
        this.isDuplicated = builder.isDuplicated;
        this.fmKoreaArticleDto = builder.fmKoreaArticleDto;
        this.nextPageUrl = builder.nextPageUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean isDuplicated;
        private FmKoreaArticleDto fmKoreaArticleDto;
        private String nextPageUrl;

        public Builder isDuplicated(boolean isDuplicated) {
            this.isDuplicated = isDuplicated;
            return this;
        }

        public Builder fmKoreaArticleDto(FmKoreaArticleDto fmKoreaArticleDto) {
            this.fmKoreaArticleDto = fmKoreaArticleDto;
            return this;
        }

        public Builder nextPageUrl(String nextPageUrl) {
            this.nextPageUrl = nextPageUrl;
            return this;
        }

        public ContentCrawlingDto build() {
            return new ContentCrawlingDto(this);
        }
    }
}
