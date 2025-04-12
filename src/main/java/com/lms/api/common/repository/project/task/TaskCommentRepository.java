package com.lms.api.common.repository.project.task;

import com.lms.api.common.entity.project.task.SubTaskEntity;
import com.lms.api.common.entity.project.task.TaskCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface TaskCommentRepository extends JpaRepository<TaskCommentEntity, Long>,
    QuerydslPredicateExecutor<TaskCommentEntity> {


}
