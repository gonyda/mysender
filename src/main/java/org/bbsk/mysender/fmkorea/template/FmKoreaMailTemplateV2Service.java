package org.bbsk.mysender.fmkorea.template;

import org.bbsk.mysender.fmkorea.dto.FmKoreaArticleDto;
import org.bbsk.mysender.fmkorea.template.utils.FmKoreaTemplateUtilsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FmKoreaMailTemplateV2Service {

    private final FmKoreaTemplateUtilsService fmKoreaTemplateUtilsService;

    public FmKoreaMailTemplateV2Service(FmKoreaTemplateUtilsService fmKoreaTemplateUtilsService) {
        this.fmKoreaTemplateUtilsService = fmKoreaTemplateUtilsService;
    }

    public String getHtmlForMail(List<FmKoreaArticleDto> articles) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html lang=\"ko\">");
        sb.append("<head>");
        sb.append("    <meta charset=\"UTF-8\">");
        sb.append("    <style>");
        sb.append("        body {font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; margin: 0;}");
        sb.append("        .container {max-width: 600px; margin: auto;}");
        sb.append("        .mail-section {background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); margin-bottom: 20px; overflow: hidden;}");
        sb.append("        .mail-header {background-color: #ffffff; color: #333333; padding: 15px; border-bottom: 1px solid #e0e0e0;}");
        sb.append("        .mail-title {font-size: 24px; font-weight: bold; color: black;}");
        sb.append("        .mail-time {font-size: 14px; color: #888888; margin-top: 10px;}");
        sb.append("        .mail-body {padding: 15px; color: #333333; font-size: 16px; line-height: 1.6;}");
        sb.append("        .email-image {max-width: 100%; height: auto; display: block; margin: 15px auto;}");
        sb.append("        .search_keyword_bg_yellow, .searchContextDoc {color: darkorange; font-weight: bold;}");
        sb.append("    </style>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("    <div class=\"container\">");
        //                 section
        for (FmKoreaArticleDto article : articles) {
            sb.append("        <div class=\"mail-section\">");
            sb.append("            <div class=\"mail-header\">");
            //                         제목
            sb.append("                <a href=\"").append(article.getLink()).append("\" class=\"mail-title\">");
            sb.append(                      article.getTitle());
            sb.append("                </a>");
            //                         작성시간
            sb.append("                <div class=\"mail-time\">");
            sb.append(                      article.getPostingTime());
            sb.append("                </div>");
            sb.append("            </div>");
            //                     본문 내용
            sb.append("            <div class=\"mail-body\">");
            sb.append(                  fmKoreaTemplateUtilsService.setFormatContent(article.getContent()));
            sb.append("            </div>");
            sb.append("        </div>");
        }
        sb.append("    </div>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }


}
