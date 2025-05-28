package com.lms.api.admin.batch.step.processor;

import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.File.dto.OldFileInfo;
import com.lms.api.admin.batch.step.dto.EmailSendInfo;
import com.lms.api.admin.batch.step.dto.UserOldFiles;
import com.lms.api.admin.email.dto.SendEmailRequest;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OldFileInfoToEmailRequestProcessor implements ItemProcessor<UserOldFiles, SendEmailRequest> {

    private final UserRepository userRepository;
    private final S3FileStorageService s3FileStorageService;
    @Override
    public SendEmailRequest process(UserOldFiles userOldFiles) throws Exception {
        log.info("✅ processor 메서드 진입 userOldFiles : {}" , userOldFiles);

        String userId = userOldFiles.getUserId();
        String email = userRepository.findById(userId)
                .map(UserEntity::getEmail)
                .orElse(null);

        String subject = "[오래된 파일 처리 예정 안내 드립니다 ]" ;
        String body = buildBody (userOldFiles.getOldFiles());

        return SendEmailRequest.builder()
                .userId(userId)
                .email(email)
                .subject(subject)
                .body(body)
                .build();

    }

    private String buildBody(List<OldFileInfo> files) {
        StringBuilder body  = new StringBuilder ();

        body.append("<p>안녕하세요.</p>");
        body.append("<p>아래 파일들이 <strong>1일 후 자동 삭제</strong>될 예정입니다.</p>");
        body.append("<p>필요한 파일은 아래 링크를 통해 미리 다운로드 부탁드립니다.</p><br>");

        return body.toString();
    }

}
