package io.exchange.core.config;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import io.exchange.domain.enums.Phase;
import lombok.extern.slf4j.Slf4j;

@Configuration
@DependsOn("coreConfig")
@Slf4j
public class EmailConfig {

    private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final String SMTP_PORT = "465";
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    @Bean
    public Session getSession() throws AddressException, MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        Environment env = CoreConfig.env;
        String username = env.getProperty("spring.mail.username");
        String password = env.getProperty("spring.mail.password");

        props.put("mail.smtp.auth", "true");
        //props.put("mail.debug", "true");
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.socketFactory.fallback", "false");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        return session;
    }


    public static final String IS_MAIL_SEND = "is.mail.send";

    /* JavaMailSender Configuration */
//    @Bean
//    public JavaMailSenderImpl getJavaMailSender() throws MessagingException {
//
//        Environment env = CoreConfig.env;
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//
//        mailSender.setHost(env.getProperty("spring.mail.host"));
//        mailSender.setPort(Integer.parseInt(env.getProperty("spring.mail.port")));
//        mailSender.setProtocol(env.getProperty("spring.mail.protocol"));
//        mailSender.setUsername(env.getProperty("spring.mail.username"));
//        mailSender.setPassword(env.getProperty("spring.mail.password"));
//
//        Properties props = mailSender.getJavaMailProperties();
////        props.put("mail.debug", "true");
//
//        // timeout limit for tsl e.g. stmp
//        props.put("mail.smtp.timeout", 2000);
//        props.put("mail.smtp.connectiontimeout", 2000);
//
//        // timeout limit for ssl e.g. smtps
//        props.put("mail.smtps.timeout", 2000);
//        props.put("mail.smtps.connectiontimeout", 2000);
//        //props.put("mail.transport.protocol", "smtp");
//        //props.put("mail.smtp.auth", "true");
//        //props.put("mail.smtp.starttls.enable", "true");
//        //props.put("mail.smtps.timeout", "8000");
////
////        if(CoreConfig.currentPhase.isEqual(Phase.LOCAL)) {
////            log.debug("before mail server login.");
////            LocalDateTime now = LocalDateTime.now();
////            try {
////                mailSender.testConnection();
////                log.debug("mail server login successed.");
////            } catch (Exception ex){
////                log.debug("mail server connection try time:{}sec", now.until(LocalDateTime.now(), ChronoUnit.SECONDS)); 
////                props.put(IS_MAIL_SEND, false);
////                log.error(ex.getClass().getName(), ex);
////                try {
////                    throw new Exception(null, null);
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
////            }
////        }
//
//        return mailSender;
//    }

}
