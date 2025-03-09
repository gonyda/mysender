package org.bbsk.mysender.gmail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class GmailService {

    private static final Logger log = LoggerFactory.getLogger(GmailService.class);

    private final JavaMailSender mailSender;

    public GmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Gmail 발송
     * @param to 받는 사람
     * @param title 메일 제목
     * @param content 메일 본문
     */
    public void sendEmail(String to, String title, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content, true);
            helper.setFrom("bbsk3939@gmail.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }
}
