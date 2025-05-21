package com.lms.api.common.repository.project;

import com.lms.api.admin.project.enums.ProjectFunctionType;
import com.lms.api.common.entity.project.FunctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface FunctionRepository extends JpaRepository<FunctionEntity, ProjectFunctionType>,
    QuerydslPredicateExecutor<FunctionEntity> {
}
