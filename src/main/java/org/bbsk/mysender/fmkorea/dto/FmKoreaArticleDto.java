package org.bbsk.mysender.fmkorea.dto;


public class FmKoreaArticleDto {
    private final String keyword;
    private final String title;
    private final String postingTime;
    private final String link;
    private final String content;

    public String getKeyword() {
        return keyword;
    }

    public String getTitle() {
        return title;
    }

    public String getPostingTime() {
        return postingTime;
    }

    public String getLink() {
        return link;
    }

    public String getContent() {
        return content;
    }

    private FmKoreaArticleDto(Builder builder) {
        this.keyword = builder.keyword;
        this.title = builder.title;
        this.postingTime = builder.postingTime;
        this.link = builder.link;
        this.content = builder.content;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String keyword;
        private String title;
        private String postingTime;
        private String link;
        private String content;

        public Builder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder postingTime(String postingTime) {
            this.postingTime = postingTime;
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

        public FmKoreaArticleDto build() {
            return new FmKoreaArticleDto(this);
        }
    }
}