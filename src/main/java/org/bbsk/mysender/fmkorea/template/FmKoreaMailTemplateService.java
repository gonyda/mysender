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
        sb.append(".email-image {text-align: center; margin-top: 20px;}");
        sb.append(".email-image img {max-width: 100%; height: auto; border-radius: 5px; display: block; margin: 0 auto;}");
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
                            .append(addHighlight(article.getTitle(), article.getKeyword()))
                            .append("</a> ");
                    // 작성시간
                    sb.append("<span class=\"email-time\">").append(article.getCreatedTime()).append("</span>");
                sb.append("</div>");
                // 글 본문
                sb.append("<div class=\"email-body\">").append(addHighlight(article.getContent(), article.getKeyword()))
                        .append("</div>");
                // 이미지 출력
                if(article.getImageUrlList() != null && !article.getImageUrlList().isEmpty()) {
                    sb.append("<div class=\"email-image\">");
                    for (String imgUrl : article.getImageUrlList()) {
                        sb.append("<img src=\"").append(imgUrl).append("\">");
                    }
                    sb.append("</div>");
                }
            sb.append("</div>");
        }
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }

    private static String addHighlight(String text, String keyword) {
        if (text == null) {
            return "";
        }

        // 공백 처리 및 태그 출력 문제 해결
        text = text.replaceAll("\\s*<br>\\s*", "\n") // 이미 존재하는 <br> 태그를 개행 문자로 변환 (있다면)
                   .replaceAll("\n", "<br>");       // 개행 문자 변환

        if (StringUtils.isNotEmpty(keyword)) {
            // 키워드 강조 추가
            text = text.replaceAll(keyword, "<span class=\"highlight\">" + keyword + "</span>");
        }

        return text;
    }
}
