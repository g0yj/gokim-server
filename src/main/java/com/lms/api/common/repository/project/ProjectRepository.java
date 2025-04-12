package com.lms.api.common.repository.project;

import com.lms.api.common.entity.project.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ProjectRepository extends JpaRepository<ProjectEntity, String>,
    QuerydslPredicateExecutor<ProjectEntity> {


}
