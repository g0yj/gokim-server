package com.lms.api.admin.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@Getter
public class EmailConfig {

    private final EmailProperties emailProperties;

    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailProperties.getHost());
        mailSender.setPort(emailProperties.getPort());
        mailSender.setUsername(emailProperties.getUsername());
        mailSender.setPassword(emailProperties.getPassword());

        // JavaMail 동작을 세부 설정 하는 단계 (개발 중엔 켜두고, 운영에서는 꺼두기)
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", emailProperties.getSmtp().isAuth());
        props.put("mail.smtp.starttls.enable", emailProperties.getSmtp().getStarttls().isEnable());
        props.put("mail.smtp.ssl.enable", false); // 필요한 경우 true (구글은 ssl 사용 x)
        props.put("mail.smtp.ssl.trust", "*");
        props.put("mail.debug", true);

        mailSender.setJavaMailProperties(props);
        return mailSender;
    }


}
