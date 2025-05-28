package com.lms.api.admin.batch;

import com.lms.api.admin.board.notice.NoticeService;
import com.lms.api.admin.project.ProjectService;
import com.lms.api.admin.project.file.ProjectFileService;
import com.lms.api.admin.project.task.TaskService;
import com.lms.api.admin.user.UserService;
import com.lms.api.admin.user.dto.CreateUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
//@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // H2로 자동 대체(외부 DB 무시하고 H2사용)
@Transactional // 테스트 끝나면 rollback
@RequiredArgsConstructor
public class FileCleanupEmailNotifyJobTest {

    private final UserService userService;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final ProjectFileService projectFileService;
    private final NoticeService noticeService;

    @Test
    void oldFileFindAndEmailSend_오래된파일찾아해당회원에이메일전송(){
        //1. 회원 생성
        //2. 프로젝트 생성

        //3. task 등록(파일포함)

        //4. file 등록(파일포함)

        //5. notice 등록 (파일포함)

        //6. step test
    }


}
