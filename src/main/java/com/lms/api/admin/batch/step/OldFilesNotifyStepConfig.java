package com.lms.api.admin.batch.step;

import com.lms.api.admin.batch.step.dto.EmailSendInfo;
import com.lms.api.admin.batch.step.dto.UserOldFiles;
import com.lms.api.admin.batch.step.listener.EmailSendSkipListener;
import com.lms.api.admin.batch.step.processor.OldFileInfoToEmailRequestProcessor;
import com.lms.api.admin.batch.step.reader.UserOldFilesInfoReader;
import com.lms.api.admin.batch.step.writer.OldFilesEmailNotificationWriter;
import com.lms.api.admin.email.dto.SendEmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OldFilesNotifyStepConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager; // 청크 단위 트랜잭션을 처리하기 위한 트랜잭션 매니저
    private final UserOldFilesInfoReader oldFilesInfoReader;
    private final OldFileInfoToEmailRequestProcessor oldFileInfoToEmailRequestProcessor;
    private final OldFilesEmailNotificationWriter oldFilesEmailNotificationWriter;
    private final EmailSendSkipListener emailSendSkipListener;

    /**
     * 💡 오래된 파일 알림 이메일을 전송하는 Step
     * - 회원별로 오래된 파일을 조회
     * - 이메일 전송
     * - 실패시 로그 기록
     */
    @Bean
    public Step oldFilesNotifyStep() {
        log.debug("✅ StepConfig 진입");
        return new StepBuilder("oldFilesNotifyStep", jobRepository)
                // 청크 단위 처리로 Reader가 반환하는 타입, Writer가 처리할 타입을 지정
                .<UserOldFiles, SendEmailRequest>chunk(10, platformTransactionManager)
                .reader(oldFilesInfoReader)// 입력 데이터 : 회원별 오래된 파일 목록
                .processor(oldFileInfoToEmailRequestProcessor) // OldFileInfo -> SendEmailRequest
                .writer(oldFilesEmailNotificationWriter) // 출력 처리 : 이메일 전송
                .faultTolerant() // 예외 허용 모드
                .skip(Exception.class) // 예외 발생 시 건너 뜀
                .skipLimit(50) // 최대 50건까지 skip 허용
                // 리스너의 종류(인터페이스)는 다양함! 스프링 배치가 알아서 판단해서 실행하니까 신경 쓰지 않아도 됨 . 주의 사항 아래에
                .listener(emailSendSkipListener) // skip 발생 시 처리할 리스너
                .listener(oldFilesInfoReader) // step이 종료할 때 처리할 리스너
                .build();
    }
}

/**
 *  같은 인터페이스를 여러 개 등록할 경우 (@StepScope로 동적으로 주입하거나 익명 객체로 만들 때)
    그땐 순서가 실행 로직에 영향을 줄 수 있어서 명확히 컨트롤하고 싶다면 CompositeXXXListener 써야
 */