package com.lms.api.common.repository;

import com.lms.api.admin.auth.enums.LoginType;
import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.common.entity.BatchResultLogEntity;
import com.lms.api.common.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface BatchResultLogRepository extends JpaRepository<BatchResultLogEntity, Long>,
    QuerydslPredicateExecutor<BatchResultLogEntity> {

}
