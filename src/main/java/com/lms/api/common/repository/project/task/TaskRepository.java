package com.lms.api.common.repository.project.task;

import com.lms.api.common.entity.project.task.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, String>,
    QuerydslPredicateExecutor<TaskEntity> {

    List<TaskEntity> findByFunctionEntity_Id(String functionId);

    int countByFunctionEntity_Id(String functionId);



}
