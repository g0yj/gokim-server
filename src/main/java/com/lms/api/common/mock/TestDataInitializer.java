package com.lms.api.common.mock;

import com.lms.api.common.dto.Role;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.common.repository.project.ProjectMemberRepository;
import com.lms.api.common.repository.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestDataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        log.debug("샘플 데이터 삽입 시작 ..");

        UserEntity owner = createUserIfNotExists("SampleOwner", "1234", "프로젝트소유자", "jinyjgo@gmail.com", "01091901376");
        UserEntity member1 = createUserIfNotExists("SampleMember1", "1234", "프로젝트멤버1", "jinyjgo@naver.com", "01079168787");
        UserEntity member2 = createUserIfNotExists("SampleMember2", "1234", "프로젝트멤버2", null, null);

        ProjectEntity project = ProjectEntity.builder()
                .id("P1234")
                .projectName("샘플 프로젝트")
                .userEntity(owner)
                .createdBy(owner.getId())
                .build();
        projectRepository.save(project);

        addProjectMember(project, owner, Role.OWNER);
        addProjectMember(project, member1, Role.MEMBER);
        addProjectMember(project, member2, Role.MEMBER);

        log.debug("샘플 데이터 삽입 완료");
    }

    /**
     * 유저가 존재하지 않으면 새로운 유저를 추가하는 메서드
     */
    private UserEntity createUserIfNotExists(String id, String rawPassword, String name, String email, String phone) {
        return userRepository.findById(id).orElseGet(() -> {
            String encodedPassword = passwordEncoder.encode(rawPassword);
            UserEntity user = UserEntity.builder()
                    .id(id)
                    .password(encodedPassword)
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .build();
            return userRepository.save(user);
        });
    }


    private void addProjectMember(ProjectEntity project, UserEntity user, Role role) {
        ProjectMemberEntity member = ProjectMemberEntity.builder()
                        .projectId(project.getId())
                        .projectMemberId(user.getId())
                        .role(role)
                        .projectEntity(project)
                        .userEntity(user)
                        .build();
        projectMemberRepository.save(member);
    }

}
