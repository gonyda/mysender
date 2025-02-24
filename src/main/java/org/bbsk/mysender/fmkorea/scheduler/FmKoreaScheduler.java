package org.bbsk.mysender.fmkorea.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bbsk.mysender.crawler.SeleniumUtils;
import org.bbsk.mysender.fmkorea.dto.FmKoreaMailDto;
import org.bbsk.mysender.fmkorea.entity.FmKoreaSearchKeyword;
import org.bbsk.mysender.fmkorea.repository.FmKoreaSearchKeywordRepository;
import org.bbsk.mysender.fmkorea.service.FmKoreaService;
import org.bbsk.mysender.gmail.service.GmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FmKoreaScheduler {

    private final FmKoreaService fmKoreaService;
    private final GmailService gmailService;
    private final FmKoreaSearchKeywordRepository fmKoreaSearchKeywordRepository;

    @Scheduled(cron = "0 19 * * * ?")
    public void getFmKoreaSearchKeywordByStock() {
        List<List<FmKoreaMailDto>> mailList = new ArrayList<>();
        List<FmKoreaSearchKeyword> keywordList = fmKoreaSearchKeywordRepository.getFmKoreaSearchKeywordByUseYn("Y");

        log.info("## Start");
        log.info("## Keyword List: {}", StringUtils.join(keywordList.stream().map(FmKoreaSearchKeyword::getKeyword).toArray(), ", "));

        for (FmKoreaSearchKeyword entity : keywordList) {
            log.info("## Current Keyword : {}", entity.getKeyword());
            mailList.add(fmKoreaService.getFmKoreaSearchKeywordByStock(SeleniumUtils.getChromeDriver(), entity.getKeyword()));
        }

        if(!mailList.isEmpty()) {
            for (List<FmKoreaMailDto> mail : mailList) {
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
                for (FmKoreaMailDto dto : mail) {
                    sb.append("<div class=\"email-post\">");
                    sb.append("<div class=\"email-header\">");
                        // 제목
                        sb.append("<a href=\"").append(dto.getLink()).append("\" style=\"color: black\">")
                          .append(addHighlight(dto.getTitle(), dto.getKeyword()))
                          .append("</a> ");
                        // 작성시간
                        sb.append("<span class=\"email-time\">").append(dto.getCreatedTime()).append("</span>");
                    sb.append("</div>");
                    // 글 본문
                    sb.append("<div class=\"email-body\">").append(addHighlight(dto.getContent(), dto.getKeyword()))
                      .append("</div>");
                    // 이미지 출력
                    if(dto.getImageUrlList() != null && !dto.getImageUrlList().isEmpty()) {
                        sb.append("<div class=\"email-image\">");
                        for (String imgUrl : dto.getImageUrlList()) {
                            sb.append("<img src=\"").append(imgUrl).append("\">");
                        }
                        sb.append("</div>");
                    }
                    sb.append("</div>");
                }
                sb.append("</div>");
                sb.append("</body>");
                sb.append("</html>");
                gmailService.sendEmail("bbsk3939@gmail.com"
                                        , StringUtils.join(mail.get(0).getKeyword(), " 검색결과 ", mail.size(), "개")
                                        , sb.toString());
                sb = null;
            }
        }

        log.info("## End");
    }

    private String addHighlight(String text, String keyword) {
        if(StringUtils.contains(text, keyword)) {
            return text.replaceAll("\n", "<br>").replaceAll(keyword, "<span class=\"highlight\">" + keyword + "</span>");
        } else {
            return text.replaceAll("\n", "<br>");
        }
    }
}
