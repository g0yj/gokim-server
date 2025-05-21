package com.lms.api.common.repository.project.task;

import com.lms.api.common.entity.project.task.TaskStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface TaskStatusRepository extends JpaRepository<TaskStatusEntity, Long>,
    QuerydslPredicateExecutor<TaskStatusEntity> {


    List<TaskStatusEntity> findAllByNameAndProjectId(String name, String projectId);

    List<TaskStatusEntity> findByNameAndProjectId(String name, String projectId);




}
