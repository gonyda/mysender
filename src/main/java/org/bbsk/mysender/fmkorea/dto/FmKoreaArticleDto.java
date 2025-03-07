package org.bbsk.mysender.fmkorea.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class FmKoreaArticleDto {
    private final String keyword;
    private final String title;
    private final String createdTime;
    private final String link;
    private final String content;
    private final List<String> imageUrlList;

    private FmKoreaArticleDto(Builder builder) {
        this.keyword = builder.keyword;
        this.title = builder.title;
        this.createdTime = builder.createdTime;
        this.link = builder.link;
        this.content = builder.content;
        this.imageUrlList = builder.imageUrlList;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String keyword;
        private String title;
        private String createdTime;
        private String link;
        private String content;
        private List<String> imageUrlList;

        public Builder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder createdTime(String createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public Builder link(String link) {
            this.link = link;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder imageUrlList(List<String> imageUrlList) {
            this.imageUrlList = imageUrlList;
            return this;
        }

        public FmKoreaArticleDto build() {
            return new FmKoreaArticleDto(this);
        }
    }
}