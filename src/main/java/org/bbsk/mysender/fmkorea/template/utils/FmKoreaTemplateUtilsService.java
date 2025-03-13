package org.bbsk.mysender.fmkorea.template.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class FmKoreaTemplateUtilsService {


    public String setFormatContent(String text) {
        if (text == null) {
            return "";
        }
        text = setVideo(text);
        text = setImgTag(text);
        text = setIframeTag(text);

        return text;
    }

    public String setVideo(String text) {
        // HTML 파싱
        Document doc = Jsoup.parse(text);

        // 클래스가 auto_media_wrapper인 태그 선택 후 대체
        Elements elems = doc.select(".auto_media_wrapper");
        for (Element elem : elems) {
            // 새로운 div 요소를 생성
            elem.replaceWith(new Element("div").appendText("❌Video 삭제 처리 되었습니다.❌"));
        }

        // 변경된 HTML 리턴
        return doc.html();
    }

    public String setIframeTag(String text) {
        // iframe 태그가 있다면 해당 태그를 삭제하고 src만 남기기
        // 아래 정규표현식은 iframe 태그의 src 속성 값을 추출하여, 해당 iframe 태그 전체를 src 값으로 치환합니다.
        // (?i)는 대소문자 구분 없이 매칭하도록 하고, (?:></iframe>|/>)는 닫는 태그가 있는 경우와 self-closing 태그 모두를 처리합니다.
        text = text.replaceAll("(?i)<iframe[^>]*src=[\"']([^\"'>]+)[\"'][^>]*(?:></iframe>|/>)", "⏯️iframe: $1");
        return text;
    }

    public String setImgTag(String text) {
        Document doc = Jsoup.parse(text);
        for (Element img : doc.select("img")) {
            // 기존의 모든 속성 제거 전에 src 값을 저장
            String src = img.attr("src");
            // src가 "//"로 시작하면 "https:"를 추가
            if (src.startsWith("//")) {
                src = "https:" + src;
            }

            // 기존 모든 속성 제거
            img.clearAttributes();

            // 필요한 속성 추가: src, class
            img.attr("src", src);
            img.addClass("email-image");
        }

        // body 내부의 HTML만 반환
        return doc.body().html();
    }
}
