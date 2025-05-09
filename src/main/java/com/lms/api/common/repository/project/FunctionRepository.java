package com.lms.api.common.repository.project;

import com.lms.api.common.entity.project.FunctionEntity;
import com.lms.api.common.entity.project.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface FunctionRepository extends JpaRepository<FunctionEntity, String>,
    QuerydslPredicateExecutor<FunctionEntity> {

}
