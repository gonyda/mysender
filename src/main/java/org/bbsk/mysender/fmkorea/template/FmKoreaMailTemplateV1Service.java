package org.bbsk.mysender.fmkorea.template;

import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FmKoreaMailTemplateV1Service {

    public String getHtmlForSendMail(List<FmKoreaArticleDto> articles) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html lang=\"ko\">");
        sb.append("<head>");
        sb.append("<meta charset=\"UTF-8\">");
        sb.append("<style>");
        sb.append("body {font-family: Arial, sans-serif; line-height: 1.6; background-color: #f4f4f4; margin: 0; padding: 20px;}");
        sb.append(".email-container {max-width: 600px; margin: 0 auto; background: #fff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);}");
        sb.append(".email-post { padding: 15px; margin-bottom: 20px; background: #fafafa; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
        sb.append(".email-header {font-size: 24px; font-weight: bold; color: #333;}");
        sb.append(".email-time {font-size: 12px; color: #777;}");
        sb.append(".email-body {font-size: 16px; color: #444;}");
        sb.append(".email-image {text-align: center; margin-top: 20px; width: 100% !important; height: auto !important;}");
        sb.append(".search_keyword_bg_yellow, .searchContextDoc { color: darkorange; font-weight: bold; }");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");
            sb.append("<div class=\"email-container\">");
            for (FmKoreaArticleDto article : articles) {
                sb.append("<div class=\"email-post\">");
                    sb.append("<div class=\"email-header\">");
                        // 제목
                        sb.append("<a href=\"").append(article.getLink()).append("\" style=\"color: black\">")
                                .append(article.getTitle())
                                .append("</a> ");
                        // 작성시간
                        sb.append("<span class=\"email-time\">").append(article.getPostingTime()).append("</span>");
                    sb.append("</div>");
                    // 글 본문
                    sb.append("<div class=\"email-body\">").append(setFormatContent(article.getContent())).append("</div>");
                sb.append("</div>");
            }
            sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    private static String setFormatContent(String text) {
        if (text == null) {
            return "";
        }
        text = setVideo(text);
        text = setImgTag(text);
        text = setIframeTag(text);

        return text;
    }

    private static String setVideo(String text) {
        // HTML 파싱
        Document doc = Jsoup.parse(text);

        // 클래스가 auto_media_wrapper인 태그 선택 후 대체
        Elements elems = doc.select(".auto_media_wrapper");
        for (Element elem : elems) {
            // 새로운 div 요소를 생성
            elem.replaceWith(new Element("div").appendText("Video 삭제 처리 되었습니다."));
        }

        // 변경된 HTML 리턴
        return doc.html();
    }

    private static String setIframeTag(String text) {
        // iframe 태그가 있다면 해당 태그를 삭제하고 src만 남기기
        // 아래 정규표현식은 iframe 태그의 src 속성 값을 추출하여, 해당 iframe 태그 전체를 src 값으로 치환합니다.
        // (?i)는 대소문자 구분 없이 매칭하도록 하고, (?:></iframe>|/>)는 닫는 태그가 있는 경우와 self-closing 태그 모두를 처리합니다.
        text = text.replaceAll("(?i)<iframe[^>]*src=[\"']([^\"'>]+)[\"'][^>]*(?:></iframe>|/>)", "iframe: $1");
        return text;
    }

    private static String setImgTag(String text) {
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
