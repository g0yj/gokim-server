package com.lms.api.admin.batch.step.listener;

import com.lms.api.admin.File.dto.OldFileInfo;
import com.lms.api.admin.batch.step.exception.MailSendFailedException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 이메일 전송 중 예외 발생 시 로그를 기록 하기 위함
 * 이메인 전송 중 EmailSendException 발생 -> SkipListener 가 감지
 * 실패한 회원 ID + 메시지 -> DB 저장
 *
 */
@Slf4j
@Component
@Getter
public class EmailSendSkipListener implements SkipListener<OldFileInfo, OldFileInfo> {

    // 예외 발생한 경우 userId 모음
    List<String> throwUserIds = new ArrayList<>();

    @Override
    public void onSkipInRead(Throwable t) {
        // Reader 에서 건너 뛴 경우
    }

    /**
     * Writer 중 예외로 인해 Skip된 경우 호출됨
     * @param item
     * @param t
     */
    @Override
    public void onSkipInWrite(OldFileInfo item, Throwable t) {
        if ( t instanceof MailSendFailedException) {

            throwUserIds.add(item.getUserId());

            log.warn("❌ 이메일 전송 실패: userId={}, s3Key={}, 이유={}",
                    item.getUserId(), item.getS3Key(), t.getMessage());
        } else {
            log.warn("❌ 예기치 않은 Writer 예외 발생: {}", t.getMessage());
        }
    }


    @Override
    public void onSkipInProcess(OldFileInfo item, Throwable t) {
        // Processor 에서 건너뛴 경우
    }
}
