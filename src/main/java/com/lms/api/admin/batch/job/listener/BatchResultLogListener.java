package com.lms.api.admin.batch.job.listener;

import com.lms.api.admin.batch.step.listener.EmailSendSkipListener;
import com.lms.api.admin.batch.step.writer.OldFilesEmailNotificationWriter;
import com.lms.api.common.entity.BatchResultLogEntity;
import com.lms.api.common.repository.BatchResultLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Job 시작/종료 시점을 감지하고, BatchResultLogEntity에 로그를 저장하는 역할
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchResultLogListener implements JobExecutionListener {

    private final BatchResultLogRepository batchResultLogRepository;
    private final OldFilesEmailNotificationWriter oldFilesEmailNotificationWriter; // 이메일 없는 Id 리스트
    private final EmailSendSkipListener emailSendSkipListener; // 이메일 전송 시 예외 발생인 경우
    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobExecution.getExecutionContext().put("logStartTime", LocalDateTime.now()); // 실행 시작 기록용
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LocalDateTime startAt = (LocalDateTime) jobExecution.getExecutionContext().get("logStartTime");
        LocalDateTime endedAt = LocalDateTime.now();
        String status = jobExecution.getStatus().name();
        String jobName = jobExecution.getJobInstance().getJobName();

        // 이메일 없으면 userId 수집
        List<String> noEmailUserIds = oldFilesEmailNotificationWriter.getEmailNotExistUserIds();
        String noEmailNote = noEmailUserIds.isEmpty() ? "" : "이메일 없음 -> 예상 : sample1,2 | 실제 : " +String.join(",", noEmailUserIds);

        // 이메일 전송 과정에 실패한 경우 userId 수집 (-> 예외 발생한 경우)
        List<String> throwUserIds = emailSendSkipListener.getThrowUserIds();
        String throwEmailNote = throwUserIds.isEmpty() ? "": "실패 -> 예상 : 2건  | 실제 : " + throwUserIds.size() +"건: " + String.join(",", throwUserIds);

        // 배치 종료 후 메세지
        String message = "오래된 파일 제거를 회원에게 알림 메일 전송" ;
        // 배치 종료 후 비고 내용 (두 내용을 합침)
        String note = Stream.of(noEmailNote, throwEmailNote)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining("/"));


        BatchResultLogEntity logEntity = BatchResultLogEntity.builder()
                .jobName(jobName)
                .status(jobExecution.getStatus().name())
                .startedAt(startAt)
                .endedAt(endedAt)
                .status(status)
                .message(message) // 기본 메시지
                .note(note) // 기본
                .build();

        batchResultLogRepository.save(logEntity);

        log.info("📄 Batch 로그 저장 완료: jobName={}, status={}", jobName, status);

    }
}
