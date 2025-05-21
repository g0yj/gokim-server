package com.lms.api.common.repository.project;

import com.lms.api.common.entity.project.ProjectFunctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ProjectFunctionRepository extends JpaRepository<ProjectFunctionEntity, String>,
    QuerydslPredicateExecutor<ProjectFunctionEntity> {
    List<ProjectFunctionEntity> findByProjectEntity_IdOrderByProjectFunctionSortAsc(String projectId);
}
