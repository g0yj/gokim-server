package com.lms.api.admin.batch.step.writer;

import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.batch.step.exception.MailSendFailedException;
import com.lms.api.admin.email.EmailService;
import com.lms.api.admin.email.dto.SendEmailRequest;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter //Step 실행이 끝나면 그 내부 데이터를 밖에서 바로 접근 하기 위함
public class OldFilesEmailNotificationWriter implements ItemWriter<SendEmailRequest> {
    private final UserRepository userRepository;
    private final S3FileStorageService s3FileStorageService;
    private final EmailService emailService;

    // 이메일이 없는 회원 목록 (note 기록용)
    private final List<String> emailNotExistUserIds =  new ArrayList<>();

    @Override
    public void write(Chunk<? extends SendEmailRequest> items) throws Exception {
        log.info("✅ writer 메서드 진입");
        for (SendEmailRequest sendEmailRequest : items) {
            String userId = sendEmailRequest.getUserId();

            String email = userRepository.findById(userId)
                    .map(UserEntity::getEmail)
                    .orElse(null);

            if (email == null || email.isBlank()) {
                log.debug(" 이메일 없음 -> 총 2번 조회 예상 | userId: {}", userId);
                emailNotExistUserIds.add(userId);
                continue;
            }

            if(userId.equals("Admin")){
                throw new MailSendFailedException (" 일부러 예외 발생 시킴 (이메일 있고 파일 있는데 없는 척) >  " + userId + "- email: " + email);
            }
            emailService.send(sendEmailRequest);
        }
        log.debug( "✅ writer메서드 종료 (2명 예상) | 실제 목록: {}" , String.join(",", emailNotExistUserIds));
    }

}
