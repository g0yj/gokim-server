package com.lms.api.common.mock;

import com.lms.api.admin.auth.enums.LoginType;
import com.lms.api.admin.project.enums.ProjectFunctionType;
import com.lms.api.admin.project.enums.ProjectRole;
import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.id.ProjectMemberId;
import com.lms.api.common.entity.project.FunctionEntity;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.ProjectFunctionEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import com.lms.api.common.entity.project.task.SubTaskEntity;
import com.lms.api.common.entity.project.task.TaskCommentEntity;
import com.lms.api.common.entity.project.task.TaskEntity;
import com.lms.api.common.entity.project.task.TaskStatusEntity;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.common.repository.project.FunctionRepository;
import com.lms.api.common.repository.project.ProjectFunctionRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final ProjectFunctionRepository projectFunctionRepository;
    private final FunctionRepository functionRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.debug("샘플 데이터 삽입 시작 ..");

        // 기능(functionTask,functionCalendar,functionBoard,functionFile)
        FunctionEntity functionTask = createFunctionIfNotExists(ProjectFunctionType.TASK, "보드");
        FunctionEntity functionCalendar = createFunctionIfNotExists(ProjectFunctionType.CALENDAR, "캘린더");
        FunctionEntity functionBoard = createFunctionIfNotExists(ProjectFunctionType.BOARD, "게시판");
        FunctionEntity functionFile = createFunctionIfNotExists(ProjectFunctionType.FILE, "첨부파일");
        FunctionEntity functionPage = createFunctionIfNotExists(ProjectFunctionType.PAGE, "빈페이지");
        // 회원(owner,member1,member2,member3, admin)
        UserEntity user1 = createUserIfNotExists("SampleOwner", "1234", "프로젝트소유자", "duswls3000@gmail.com", "01091901376", UserRole.USER, LoginType.NORMAL);
        UserEntity user2 = createUserIfNotExists("SampleMember1", "1234", "프로젝트멤버1", "jinyjgo@naver.com", "01079168787", UserRole.USER, LoginType.NORMAL);
        UserEntity user3 = createUserIfNotExists("SampleMember2", "1234", "프로젝트멤버2", null, null, UserRole.USER, LoginType.NORMAL);
        UserEntity user4 = createUserIfNotExists("SampleMember3", "1234", "프로젝트멤버3", null, null, UserRole.USER, LoginType.NORMAL);
        UserEntity admin = createUserIfNotExists("Admin", "1234", "관리자", "goyeonjin@naver.com", null, UserRole.ADMIN, LoginType.NORMAL);
        // 프로젝트(project)
        ProjectEntity project = createProjectIfNotExists("P1234", "샘플 프로젝트", user1);
        // 프로젝트 멤버(owner, member1, member2, member3)
        ProjectMemberEntity owner = createProjectMemberIfNotExists(project, user1, ProjectRole.OWNER);
        ProjectMemberEntity member1 = createProjectMemberIfNotExists(project, user2, ProjectRole.MEMBER);
        ProjectMemberEntity member2 = createProjectMemberIfNotExists(project, user3, ProjectRole.MEMBER);
        ProjectMemberEntity member3 = createProjectMemberIfNotExists(project, user4, ProjectRole.MEMBER);
        // 프로젝트 기능 (board ,task, calendar)
        ProjectFunctionEntity board = createProjectFunctionIfNotExists("PF01", "게시판11", functionBoard.getProjectFunctionType(), 1, project);
        ProjectFunctionEntity task = createProjectFunctionIfNotExists("PF02", "보드11", functionTask.getProjectFunctionType(), 2, project);
        ProjectFunctionEntity calendar = createProjectFunctionIfNotExists("PF03", "캘린더11", functionCalendar.getProjectFunctionType(), 3, project);
        ProjectFunctionEntity file = createProjectFunctionIfNotExists("PF04", "파일모음", functionFile.getProjectFunctionType(), 4, project);
        // task 상태(task) : 기본값 먼저 있으면 실행
        TaskStatusEntity idea = createTaskStatusIfNotExists("Idea", project , task.getId());
        TaskStatusEntity todo = createTaskStatusIfNotExists("Todo", project , task.getId());
        TaskStatusEntity inProgress = createTaskStatusIfNotExists("InProgress", project , task.getId());
        TaskStatusEntity done = createTaskStatusIfNotExists("Done", project, task.getId());
        // task 추가
        TaskEntity task1 = createTaskIfNotExists("T1111", "테이블 설계", "테이블 설계에 관련된 내용입니다. 추후 에디터 사용", 1, owner.getUserEntity().getId(), board, idea, owner.getUserEntity().getId());
        TaskEntity task2 = createTaskIfNotExists("T2222", "추가 기획", "챗봇 추가 계획", 2, owner.getUserEntity().getId(), board, todo, owner.getUserEntity().getId());
        TaskEntity task3 = createTaskIfNotExists("T3333", "리액트 api와 연결", "현재 mock 데이터 사용 중", 3, member1.getUserEntity().getId(), board, idea, owner.getUserEntity().getId());
        TaskEntity task4 = createTaskIfNotExists("T4444", "회의록 정리", "별도로 캘린더를 만들어서 운영", 4, member2.getUserEntity().getId(), board, inProgress, owner.getUserEntity().getId());
        TaskEntity task5 = createTaskIfNotExists("T5555", "순서 변환 라이브러리 사용", "설명해주기", 5, member3.getUserEntity().getId(), board, done, owner.getUserEntity().getId());

        // 하위 항목
        createSubTaskIfNotExists("첫번째 하위 항목입니다.", task1, idea, member1);
        createSubTaskIfNotExists("두번째 하위 항목입니다.", task1, done, member2);
        createSubTaskIfNotExists("세번째 하위 항목입니다.", task1, done, member2);
        // 댓글
        createTaskCommentIfNotExists("첫번째 댓글!!", task1, member2.getUserEntity().getId());
        createTaskCommentIfNotExists("두번째 댓글!!", task1, member2.getUserEntity().getId());
        createTaskCommentIfNotExists("두번째 댓글!!", task1, member3.getUserEntity().getId());


        log.debug("샘플 데이터 삽입 완료");
    }

    @Transactional
    private FunctionEntity createFunctionIfNotExists(ProjectFunctionType type, String name) {
        return functionRepository.findById(type)
                .orElseGet(() -> {
                    FunctionEntity function = FunctionEntity.builder()
                            .projectFunctionType(type)
                            .functionName(name)
                            .build();
                    return functionRepository.save(function);
                });
    }
    @Transactional
    private UserEntity createUserIfNotExists(String id, String rawPassword, String name, String email, String phone, UserRole role, LoginType loginType) {
        return userRepository.findById(id)
                .orElseGet(() -> {
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

    @Transactional
    private ProjectEntity createProjectIfNotExists(String projectId, String projectName, UserEntity owner) {
        return projectRepository.findById(projectId)
                .orElseGet(() -> {
                    ProjectEntity project = ProjectEntity.builder()
                            .id(projectId)               // ID 직접 세팅 필수
                            .projectName(projectName)
                            .userEntity(owner)
                            .build();
                    return projectRepository.save(project);
                });
    }

    @Transactional
    private ProjectMemberEntity createProjectMemberIfNotExists(ProjectEntity project, UserEntity user, ProjectRole role) {
        ProjectMemberId id = new ProjectMemberId();
        id.setProjectId(project.getId());
        id.setProjectMemberId(user.getId());

        return projectMemberRepository.findById(id)
                .orElseGet(() -> {
                    ProjectMemberEntity member = ProjectMemberEntity.builder()
                            .projectId(project.getId())
                            .projectMemberId(user.getId())
                            .projectRole(role)
                            .projectEntity(project)
                            .userEntity(user)
                            .build();
                    return projectMemberRepository.save(member);
                });
    }
    @Transactional
    private TaskStatusEntity createTaskStatusIfNotExists(String name, ProjectEntity project, String projectFunctionId) {
        return taskStatusRepository.findByNameAndProjectId(name, project.getId())
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    TaskStatusEntity status = TaskStatusEntity.builder()
                            .name(name)
                            .projectFunctionId(projectFunctionId)
                            .projectId(project.getId())
                            .build();
                    return taskStatusRepository.save(status);
                });
    }


    @Transactional
    private ProjectFunctionEntity createProjectFunctionIfNotExists(
            String id, String functionName, ProjectFunctionType functionType, int sort, ProjectEntity project) {

        return projectFunctionRepository.findById(id)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    ProjectFunctionEntity projectFunction = ProjectFunctionEntity.builder()
                            .id(id)
                            .projectFunctionName(functionName)
                            .projectFunctionSort(sort)
                            .projectFunctionType(functionType)
                            .projectEntity(project)
                            .build();
                    return projectFunctionRepository.save(projectFunction);
                });
    }




    @Transactional
    private TaskEntity createTaskIfNotExists(String id, String title, String description, int sortOrder, String assignedMember,
                                             ProjectFunctionEntity projectFunction, TaskStatusEntity taskStatus, String createdBy) {
        return taskRepository.findById(id)
                .orElseGet(() -> {
                    TaskEntity task = TaskEntity.builder()
                            .id(id)
                            .title(title)
                            .description(description)
                            .sortOrder(sortOrder)
                            .assignedMember(assignedMember)
                            .projectFunctionEntity(projectFunction)
                            .taskStatusEntity(taskStatus)
                            .createdBy(createdBy)
                            .build();
                    return taskRepository.save(task);
                });
    }
    @Transactional
    private SubTaskEntity createSubTaskIfNotExists(String content, TaskEntity taskEntity,
                                                   TaskStatusEntity taskStatusEntity, ProjectMemberEntity projectMemberEntity) {
        return subTaskRepository.findByTaskEntityAndContentAndAssignee(taskEntity, content, projectMemberEntity)
                .orElseGet(() -> {
                    SubTaskEntity subTask = SubTaskEntity.builder()
                            .content(content)
                            .taskEntity(taskEntity)
                            .taskStatusEntity(taskStatusEntity)
                            .assignee(projectMemberEntity)
                            .build();
                    return subTaskRepository.save(subTask);
                });
    }
    @Transactional
    private TaskCommentEntity createTaskCommentIfNotExists(String content, TaskEntity taskEntity, String modifiedBy) {
        return taskCommentRepository.findByTaskEntityAndContent(taskEntity, content)
                .orElseGet(() -> {
                    TaskCommentEntity comment = TaskCommentEntity.builder()
                            .content(content)
                            .taskEntity(taskEntity)
                            .modifiedBy(modifiedBy)
                            .build();
                    return taskCommentRepository.save(comment);
                });
    }
}
