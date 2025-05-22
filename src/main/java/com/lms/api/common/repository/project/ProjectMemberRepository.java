package com.lms.api.common.repository.project;

import com.lms.api.admin.project.enums.ProjectRole;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import com.lms.api.common.entity.id.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, ProjectMemberId>,
    QuerydslPredicateExecutor<ProjectMemberEntity> {

    Optional<ProjectMemberEntity> findByProjectEntity_IdAndUserEntity_IdAndProjectRole(String projectId, String userId, ProjectRole role);

    // 소유자 권한 확인용
    boolean existsByProjectEntity_IdAndUserEntity_IdAndProjectRole(String projectId, String loginId, ProjectRole role);
    boolean existsByUserEntity_IdAndProjectEntity_IdAndProjectRole(String userId, String projectId, ProjectRole role);

    Optional<ProjectMemberEntity> findByProjectEntityAndUserEntity_Id(ProjectEntity projectEntity, String userId);
}
