package org.bbsk.mysender.gmail.constant;

public enum GmailEnum {

    TO("bbsk3939@gmail.com"),
    TITLE("%s 검색결과 %d개");  // %s: 키워드, %d: 결과 개수

    private final String value;

    GmailEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 이메일 제목을 포맷팅
    public String formatTitle(String keyword, int count) {
        return String.format(this.value, keyword, count);
    }
}
