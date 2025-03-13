package org.bbsk.mysender.fmkorea.template;

import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.bbsk.mysender.fmkorea.template.utils.FmKoreaTemplateUtilsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FmKoreaMailTemplateV1Service {

    private final FmKoreaTemplateUtilsService fmKoreaTemplateUtilsService;

    public FmKoreaMailTemplateV1Service(FmKoreaTemplateUtilsService fmKoreaTemplateUtilsService) {
        this.fmKoreaTemplateUtilsService = fmKoreaTemplateUtilsService;
    }

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
                    sb.append("<div class=\"email-body\">")
                            .append(fmKoreaTemplateUtilsService.setFormatContent(article.getContent()))
                      .append("</div>");
                sb.append("</div>");
            }
            sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

}
