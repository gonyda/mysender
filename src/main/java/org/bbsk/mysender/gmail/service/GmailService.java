package org.bbsk.mysender.gmail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GmailService {

    private final JavaMailSender mailSender;

    public void sendHtmlEmail(String to, String title, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content, true);
            helper.setFrom("bbsk3939@gmail.com");

            mailSender.send(message);
            log.info("## Mail 발송 성공!");
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }
}
