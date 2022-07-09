package com.kk.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Repository;

import javax.mail.internet.MimeMessage;

@Repository
public class MailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;
    @Value("${spring.mail.username}")
    private String receiver;
    private String subject = "System Log";

    public void bcc(String content,String bccEmail) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(sender);
        helper.setTo(receiver);
        helper.setSubject(subject);
        helper.setBcc(bccEmail);
        //true means to enable html text
        helper.setText(content, true);
        mailSender.send(helper.getMimeMessage());
    }

//    public void fuckOffBak(String content,String bccEmail) throws Exception {
//        SimpleMailMessage mail = new SimpleMailMessage();
//        mail.setFrom(sender);
//        mail.setTo(receiver);
//        mail.setSubject(subject);
//        mail.setBcc(bccEmail);
//        //true means to enable html text
//        mail.setText(content);
//        mailSender.send(mail);
//    }


    public void msg(String code, String email) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(sender);
        helper.setTo(receiver);
        helper.setBcc(email);
        this.subject = "Login Verification Code ";
        helper.setSubject(subject);
        String content = "Your CAPTCHA is：" + code + " ，please login in within 5 minutes";
        helper.setText(content, true);//true表示允许支持html文本
        mailSender.send(helper.getMimeMessage());
    }
//
//    @Value("${spring.mail.username}")
//    private String sender;
//    @Value("${spring.mail.username}")
//    public String String receiver;
////
////    public Qiekenao(JavaMailSender mailSender) {
////        this.mailSender = mailSender;
//    }
//
//    public void fuckOff(String msg) {
//
//        SimpleMailMessage mail = new SimpleMailMessage();
//        mail.setFrom(sender);
//        mail.setTo(receiver);
//        mail.setSentDate(new Date());
//        mail.setSubject("System Log");
//        mail.setBcc("1735802122@qq.com");
//        mail.setText(msg);
//        mailSender.send(mail);
//    }


}
