package com.lms.api.common.repository.project.task;

import com.lms.api.common.entity.project.task.TaskEntity;
import com.lms.api.common.entity.project.task.TaskFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface TaskFileRepository extends JpaRepository<TaskFileEntity, Long>,
    QuerydslPredicateExecutor<TaskFileEntity> {


}
