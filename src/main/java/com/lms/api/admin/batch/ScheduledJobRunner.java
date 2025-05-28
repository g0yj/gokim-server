package com.lms.api.admin.batch;

import com.lms.api.admin.batch.job.FileCleanupEmailNotifyJobConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.support.JobLoader;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledJobRunner {

    private final JobLauncher jobLauncher;
    private final FileCleanupEmailNotifyJobConfig fileCleanupEmailNotifyJobConfig;

    //@Scheduled(cron = "0 10 15 * * *")
    public void fileCleanupEmailNotifyJob(){
        try {
            // 항상 유니크한 값 만들기
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(fileCleanupEmailNotifyJobConfig.fileCleanupEmailNotifyJob(), jobParameters);
            log.info("✅ Job 실행 완료");
        } catch (Exception e) {
            log.debug("❌ Job 실행 실패");
        }
    }
}
