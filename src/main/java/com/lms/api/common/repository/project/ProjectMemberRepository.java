package com.lms.api.common.repository.project;

import com.lms.api.common.dto.ProjectRole;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import com.lms.api.common.entity.id.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, ProjectMemberId>,
    QuerydslPredicateExecutor<ProjectMemberEntity> {

    Optional<ProjectMemberEntity> findByProjectEntity_IdAndUserEntity_IdAndProjectRole(String projectId, String userId, ProjectRole role);


}
