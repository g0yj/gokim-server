package com.lms.api.common.repository.project.task;

import com.lms.api.common.entity.project.task.TaskCommentEntity;
import com.lms.api.common.entity.project.task.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface TaskCommentRepository extends JpaRepository<TaskCommentEntity, Long>,
    QuerydslPredicateExecutor<TaskCommentEntity> {

    boolean existsByTaskEntityAndContentAndCreatedBy(TaskEntity taskEntity, String content, String createdBy);
    Optional<TaskCommentEntity> findByTaskEntityAndContent(TaskEntity taskEntity, String content);

    TaskCommentEntity findByTaskEntity_Id(String taskId);
}
