package com.lms.api.admin.batch.job;

import com.lms.api.admin.batch.job.listener.BatchResultLogListener;
import com.lms.api.admin.batch.step.OldFilesNotifyStepConfig;
import com.lms.api.admin.batch.step.writer.OldFilesEmailNotificationWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FileCleanupEmailNotifyJobConfig {

    private final JobRepository jobRepository;
    private final OldFilesNotifyStepConfig oldFilesNotifyStepConfig;
    private final BatchResultLogListener batchResultLogListener;

    @Bean
    public Job fileCleanupEmailNotifyJob(){
        log.info("✅ JobConfig 메서드 진입 ");
        return new JobBuilder("fileCleanupEmailNotifyJob", jobRepository)
                .start(oldFilesNotifyStepConfig.oldFilesNotifyStep())
                .listener(batchResultLogListener)
                .build();
    }

}
