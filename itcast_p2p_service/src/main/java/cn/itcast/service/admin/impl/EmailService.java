package cn.itcast.service.admin.impl;

import cn.itcast.service.admin.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService implements IEmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${email.default.from}")
    private String from;

    //email发给谁 title标题 content内容
    public void sendEmail(String email,String title,String content){
       //创建一个邮箱信息
        MimeMessage mm=javaMailSender.createMimeMessage();
        MimeMessageHelper  helper=new MimeMessageHelper(mm);

        try {
            helper.setFrom("123_hrf@sina.com");
            helper.setSubject(title);
            helper.setText(content);
            helper.addTo(email);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        //发送邮件
        javaMailSender.send(mm);

    }

}
