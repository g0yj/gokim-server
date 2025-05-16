package com.lms.api.common.mock;

import com.lms.api.admin.auth.enums.LoginType;
import com.lms.api.admin.project.enums.ProjectRole;
import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.FunctionEntity;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import com.lms.api.common.entity.project.task.SubTaskEntity;
import com.lms.api.common.entity.project.task.TaskCommentEntity;
import com.lms.api.common.entity.project.task.TaskEntity;
import com.lms.api.common.entity.project.task.TaskStatusEntity;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.common.repository.project.FunctionRepository;
import com.lms.api.common.repository.project.ProjectMemberRepository;
import com.lms.api.common.repository.project.ProjectRepository;
import com.lms.api.common.repository.project.task.SubTaskRepository;
import com.lms.api.common.repository.project.task.TaskCommentRepository;
import com.lms.api.common.repository.project.task.TaskRepository;
import com.lms.api.common.repository.project.task.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestDataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final SubTaskRepository subTaskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final PasswordEncoder passwordEncoder;
    private final FunctionRepository functionRepository;

    @Override
    public void run(String... args) throws Exception {
        log.debug("샘플 데이터 삽입 시작 ..");

        // 유저 생성
        UserEntity owner = createUserIfNotExists("SampleOwner", "1234", "프로젝트소유자", "duswls3000@gmail.com", "01091901376", UserRole.USER, LoginType.NORMAL);
        UserEntity member1 = createUserIfNotExists("SampleMember1", "1234", "프로젝트멤버1", "jinyjgo@naver.com", "01079168787", UserRole.USER, LoginType.NORMAL);
        UserEntity member2 = createUserIfNotExists("SampleMember2", "1234", "프로젝트멤버2", null, null, UserRole.USER, LoginType.NORMAL);
        UserEntity member3 = createUserIfNotExists("SampleMember3", "1234", "프로젝트멤버3", null, null, UserRole.USER, LoginType.NORMAL);
        UserEntity admin = createUserIfNotExists("Admin","1234", "관리자", "goyeonjin@naver.com", null, UserRole.ADMIN, LoginType.NORMAL);

        // 프로젝트 생성
        ProjectEntity project = ProjectEntity.builder()
                .id("P1234")
                .projectName("샘플 프로젝트")
                .userEntity(owner)
                .createdBy(owner.getId())
                .build();
        projectRepository.save(project);

        // 프로젝트 초대
        ProjectMemberEntity assignee = addProjectMember(project, owner, ProjectRole.OWNER);
        addProjectMember(project, member1, ProjectRole.MEMBER);
        addProjectMember(project, member2, ProjectRole.MEMBER);

        // 기능 추가
        FunctionEntity dashboard = addFunction("PF01","대시보드", 1L , project);
        FunctionEntity board = addFunction("PF02", "할일", 2L, project);
        FunctionEntity calendar = addFunction("PF03", "캘린더", 3L, project);

        // 프로젝트 할일 등록
        List<String> statusNames = Arrays.asList("Idea", "Todo", "InProgress", "Done");
        statusNames.stream()
                .map(name -> TaskStatusEntity.builder()
                        .projectEntity(project)
                        .name(name)
                        .build())
                .forEach(taskStatusRepository::save);

        // 등록된 TaskStatusEntity를 사용하여 Task 추가
        TaskStatusEntity idea = taskStatusRepository.findByNameAndProjectEntity_Id("Idea", project.getId());
        TaskStatusEntity todo = taskStatusRepository.findByNameAndProjectEntity_Id("Todo", project.getId());
        TaskStatusEntity inProgress = taskStatusRepository.findByNameAndProjectEntity_Id("InProgress", project.getId());
        TaskStatusEntity done = taskStatusRepository.findByNameAndProjectEntity_Id("Done", project.getId());

        TaskEntity task = addTask("T1111", "테이블 설계", "테이블 설계에 관련된 내용입니다. 추후 에디터 사용", 1, board, owner, idea);
        addTask("T2222", "프로젝트 관련 API 생성", "CRUD에 대한 API 설명을 추가해주세요", 2, board, owner, todo);
        addTask("T3333", "보드 관련 API 생성", "멤버가 등록한 TODO입니다", 3, board, member1, inProgress);
        addTask("T4444", "수정 사항 확인", "수정된 내용 체크했으면 문자로 알려주세요", 4, board, member1, done);

        addSubTask("첫번째 하위 항목입니다." ,task, idea, assignee);
        addSubTask("두번째 하위 항목입니다." ,task, done, assignee);

        addTaskComment("첫번째 댓글!!",task, member2.getId());
        addTaskComment("두번째 댓글!!",task, owner.getId());

        log.debug("샘플 데이터 삽입 완료");
    }

    /**
     * 유저가 존재하지 않으면 새로운 유저를 추가하는 메서드
     */
    private UserEntity createUserIfNotExists(String id, String rawPassword, String name, String email, String phone, UserRole role , LoginType loginType) {
        return userRepository.findById(id).orElseGet(() -> {
            String encodedPassword = passwordEncoder.encode(rawPassword);
            UserEntity user = UserEntity.builder()
                    .id(id)
                    .password(encodedPassword)
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .role(role)
                    .loginType(loginType)
                    .build();
            return userRepository.save(user);
        });
    }

    /**
     * 프로젝트 멤버를 추가하는 메서드
     */
    private ProjectMemberEntity addProjectMember(ProjectEntity project, UserEntity user, ProjectRole projectRole) {
        ProjectMemberEntity member = ProjectMemberEntity.builder()
                .projectId(project.getId())
                .projectMemberId(user.getId())
                .projectRole(projectRole)
                .projectEntity(project)
                .userEntity(user)
                .build();
        projectMemberRepository.save(member);
        return member;
    }

    private FunctionEntity addFunction(String id, String functionName, Long functionSort , ProjectEntity projectEntity){
        FunctionEntity function = FunctionEntity.builder()
                .id(id)
                .functionName(functionName)
                .functionSort(functionSort)
                .projectEntity(projectEntity)
                .build();
        functionRepository.save(function);
        return function;
    }

    /**
     * Task를 추가하는 메서드
     */
    private TaskEntity addTask(String id, String title, String content, int sortOrder, FunctionEntity functionEntity, UserEntity userEntity, TaskStatusEntity taskStatusEntity ) {
        TaskEntity task = TaskEntity.builder()
                .id(id)
                .title(title)
                .description(content)
                .sortOrder(sortOrder)
                .assignedMember(userEntity.getId())
                .functionEntity(functionEntity)
                .taskStatusEntity(taskStatusEntity)
                .createdBy(userEntity.getId())
                .build();
        taskRepository.save(task);
        return task;
    }

    private void addSubTask(String content, TaskEntity taskEntity, TaskStatusEntity taskStatusEntity, ProjectMemberEntity assignee  ) {
        SubTaskEntity subTask = SubTaskEntity.builder()
                .content(content)
                .taskEntity(taskEntity)
                .taskStatusEntity(taskStatusEntity)
                .assignee(assignee)
                .build();
        subTaskRepository.save(subTask);
    }

    private void addTaskComment(String content, TaskEntity taskEntity , String userEntity){
        TaskCommentEntity taskComment = TaskCommentEntity.builder()
                .content(content)
                .taskEntity(taskEntity)
                .createdBy(userEntity)
                .build();
        taskCommentRepository.save(taskComment);
    }

}
