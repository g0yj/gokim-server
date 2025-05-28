package com.lms.api.admin.batch.step.reader;

import com.lms.api.admin.File.OldFileService;
import com.lms.api.admin.File.dto.OldFileInfo;
import com.lms.api.admin.batch.step.dto.UserOldFiles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserOldFilesInfoReader implements ItemReader<UserOldFiles> , StepExecutionListener {

    private final OldFileService oldFileService;

    // Spring Batch의 ItemReader는 한 번에 하나씩 데이터를 반환해야 하기 때문에 바꿔야됨! 아래 read()랑 연관. T read throws Exception 형태임
    private Iterator<UserOldFiles> iterator ;


    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("✅ Step Reader 진입");
        LocalDateTime threshold = LocalDateTime.now().minusHours(5); // 하루 전

        log.info("조회 시간은? -> {}", threshold);
        Map<String, List<OldFileInfo>> groupedFiles = oldFileService.findOldFilesGroupedByUser(threshold);

        List<UserOldFiles> result = groupedFiles.entrySet().stream()
                .map( e -> new UserOldFiles(e.getKey(), e.getValue()))
                .toList();

        this.iterator = result.iterator();
    }

    @Override
    public UserOldFiles read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (iterator != null && iterator.hasNext()) {
            UserOldFiles next = iterator.next();
            log.debug("📥 Reader 반환: {}", next);
            return next;
        } else {
            log.debug("📭 Reader: 더 이상 데이터 없음 (null 반환)");
            return null;
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED; // Step 또는 Job이 끝났을 때 상태를 나타내는 값
    }


}
/**
 * afterStep이란?
 *  - Step이 끝나면 정상 종료 상태로 간주한다! 이 결과는 자동으로 DB의 메타 테이블에 저장됨.
 *  - step은 reader , processor , writer 로 실행됨.
 *  - 모든 단계가 끝나고 step이 종료될 때 실행됨!
 *  - 단순히 reader 클래스 안에 적는다고 자동으로 실행되는거 아님
 *  - Step을 한번에 실행하도록 만든 StepExecutionListener 에 등록해야됨. 참고 : oldFilesNotifyStep()
 */