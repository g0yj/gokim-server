package com.lms.api.common.repository.project.task;

import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.task.SubTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface SubTaskRepository extends JpaRepository<SubTaskEntity, Long>,
    QuerydslPredicateExecutor<SubTaskEntity> {


}
