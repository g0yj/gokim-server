package com.lms.api.common.repository.project.file;

import com.lms.api.admin.project.enums.ProjectFunctionType;
import com.lms.api.common.entity.project.FunctionEntity;
import com.lms.api.common.entity.project.file.ProjectFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectFileRepository extends JpaRepository<ProjectFileEntity, String>,
    QuerydslPredicateExecutor<ProjectFileEntity> {

    // entities 의 갯수를 구하기 위함
    int countByProjectFunctionEntity_Id(String projectFunctionId);
}
