package com.lms.api.admin.email;


import com.lms.api.admin.email.dto.SendEmailRequest;
import com.lms.api.admin.batch.step.exception.MailSendFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailProperties emailProperties;

    public void send(SendEmailRequest emailRequest) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            boolean hasFiles = emailRequest.getFiles() != null && !emailRequest.getFiles().isEmpty();
            boolean hasLinks = emailRequest.getLinkFiles() != null && !emailRequest.getLinkFiles().isEmpty();

            MimeMessageHelper helper = new MimeMessageHelper(message, hasFiles, "UTF-8");

            helper.setFrom(emailProperties.getUsername());
            helper.setTo(emailRequest.getEmail());
            helper.setSubject(emailRequest.getSubject());

            // 본문 + 링크 조합
            StringBuilder bodyBuilder = new StringBuilder();
            bodyBuilder.append(emailRequest.getBody() == null ? "" : emailRequest.getBody());

            if (hasLinks) {
                String links = String.join("<br>", emailRequest.getLinkFiles());
                bodyBuilder.append("<br><br><strong>📎 다운로드 링크:</strong><br>").append(links);
            }

            // ✅ HTML 메일로 전송
            helper.setText(bodyBuilder.toString(), true);

            // 첨부파일 처리
            if (hasFiles) {
                for (MultipartFile multipartFile : emailRequest.getFiles()) {
                    if (multipartFile.isEmpty()) {
                        log.warn("❗ 빈 첨부 파일은 무시됨: {}", multipartFile.getOriginalFilename());
                        continue;
                    }
                    helper.addAttachment(multipartFile.getOriginalFilename(), multipartFile);
                }
            }

            javaMailSender.send(message);

            log.info("✅ 메일 전송 성공: from={}, to={}, subject={}, text={}, fileCount={}",
                    emailProperties.getUsername(),
                    emailRequest.getEmail(),
                    emailRequest.getSubject(),
                    bodyBuilder,
                    hasFiles ? emailRequest.getFiles().size() : 0
            );

        } catch (MessagingException e) {
            throw new MailSendFailedException("메일 전송 실패: " + e.getMessage(), e);

        } catch (Exception e) {
            throw new MailSendFailedException("메일 전송 중 예기치 않은 오류 발생", e);
        }
    }

}



