package io.exchange.core.service;

import java.time.LocalDateTime;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import io.exchange.core.config.CoreConfig;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.Code;
import io.exchange.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String LOGO_IMG_PATH = "/resources/img/logo.png";

    private final Session session;
    private final TemplateEngine templateEngine;

    public void sendEmailByAdmin(String toEmail, String targetUrl, String title, LocalDateTime reqDate, String... messages) {

        try {

            MimeMessage mimeMsg = new MimeMessage(session);
            mimeMsg.setFrom(new InternetAddress(CoreConfig.emailNoreply));
            mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            mimeMsg.setSubject(title);

            String baseUrl = StaticUtils.getBaseUrl();
            Context context = new Context();
            context.setVariable("baseUrl", baseUrl);
            context.setVariable("logoImgPath", LOGO_IMG_PATH);
            context.setVariable("reqDate", StaticUtils.yyyyUnderMMUnderddFormatter(reqDate));
            context.setVariable("title", title);
            context.setVariable("userEmail", toEmail);
            context.setVariable("targetUrl", targetUrl);
            context.setVariable("messages", messages);
            String html = templateEngine.process("supportEmailTemplate", context);

            BodyPart mimeMsgBodyPart = new MimeBodyPart(); 
            mimeMsgBodyPart.setContent(html, "text/html; charset=" + CoreConfig.DEFAULT_CHARSET);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeMsgBodyPart);
            mimeMsg.setContent(multipart, "text/html; charset=" + CoreConfig.DEFAULT_CHARSET);

            Transport.send(mimeMsg);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(Code.EMAIL_SEND_FAIL);
        }

    }
}
