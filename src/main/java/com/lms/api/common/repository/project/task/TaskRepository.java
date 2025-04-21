package com.lms.api.common.repository.project.task;

import com.lms.api.common.entity.project.task.SubTaskEntity;
import com.lms.api.common.entity.project.task.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, String>,
    QuerydslPredicateExecutor<TaskEntity> {

    List<TaskEntity> findByProjectEntity_Id(String projectId);

    int countByProjectEntity_Id(String projectId);



}
