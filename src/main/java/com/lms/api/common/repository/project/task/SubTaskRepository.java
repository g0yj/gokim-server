package com.lms.api.common.repository.project.task;

import com.lms.api.common.entity.project.ProjectMemberEntity;
import com.lms.api.common.entity.project.task.SubTaskEntity;
import com.lms.api.common.entity.project.task.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


import java.util.Optional;

public interface SubTaskRepository extends JpaRepository<SubTaskEntity, Long>,
    QuerydslPredicateExecutor<SubTaskEntity> {
    //List<SubTaskEntity> findByTaskEntityAndContentAndAssignee(TaskEntity taskEntity, String content, ProjectMemberEntity assignee);
    Optional<SubTaskEntity> findByTaskEntityAndContentAndAssignee(TaskEntity taskEntity, String content, ProjectMemberEntity assignee);
}
