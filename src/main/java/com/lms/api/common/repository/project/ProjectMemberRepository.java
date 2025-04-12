package com.lms.api.common.repository.project;

import com.lms.api.common.entity.project.ProjectMemberEntity;
import com.lms.api.common.entity.id.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, ProjectMemberId>,
    QuerydslPredicateExecutor<ProjectMemberEntity> {


}
