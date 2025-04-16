package com.lms.api.common.repository.project.task;

import com.lms.api.common.entity.project.task.TaskStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface TaskStatusRepository extends JpaRepository<TaskStatusEntity, String>,
    QuerydslPredicateExecutor<TaskStatusEntity> {


}
