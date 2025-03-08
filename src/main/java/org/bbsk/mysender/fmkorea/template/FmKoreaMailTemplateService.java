package org.bbsk.mysender.fmkorea.template;

import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FmKoreaMailTemplateService {

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
        sb.append(".highlight { color: darkorange; font-weight: bold; }");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");
            sb.append("<div class=\"email-container\">");
            for (FmKoreaArticleDto article : articles) {
                sb.append("<div class=\"email-post\">");
                    sb.append("<div class=\"email-header\">");
                        // 제목
                        sb.append("<a href=\"").append(article.getLink()).append("\" style=\"color: black\">")
                                .append(setFormatContent(article.getTitle(), article.getKeyword()))
                                .append("</a> ");
                        // 작성시간
                        sb.append("<span class=\"email-time\">").append(article.getCreatedTime()).append("</span>");
                    sb.append("</div>");
                    // 글 본문
                    sb.append("<div class=\"email-body\">").append(setFormatContent(article.getContent(), article.getKeyword())).append("</div>");
                sb.append("</div>");
            }
            sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    private static String setFormatContent(String text, String keyword) {
        if (text == null) {
            return "";
        }
        text = setImgTag(text);
        text = setIframeTag(text);
        text = setHighLightByKeyword(text, keyword);

        return text;
    }

    private static String setHighLightByKeyword(String text, String keyword) {
        if (StringUtils.isNotEmpty(keyword)) {
            // 키워드 강조 추가
            text = text.replaceAll(keyword, "<span class=\"highlight\">" + keyword + "</span>");
        }
        return text;
    }

    private static String setIframeTag(String text) {
        // iframe 태그가 있다면 해당 태그를 삭제하고 src만 남기기
        // 아래 정규표현식은 iframe 태그의 src 속성 값을 추출하여, 해당 iframe 태그 전체를 src 값으로 치환합니다.
        // (?i)는 대소문자 구분 없이 매칭하도록 하고, (?:></iframe>|/>)는 닫는 태그가 있는 경우와 self-closing 태그 모두를 처리합니다.
        text = text.replaceAll("(?i)<iframe[^>]*src=[\"']([^\"'>]+)[\"'][^>]*(?:></iframe>|/>)", "iframe: $1");
        return text;
    }

    private static String setImgTag(String text) {
        // 상대 경로 이미지 수정 (// 로 시작하는 이미지의 경우 https: 추가)
        text = text.replaceAll("src=\"//", "src=\"https://");
        // <img> 태그에 class="email-image" 추가
        text = text.replaceAll("<img ", "<img class=\"email-image\" ");
        return text;
    }
}
