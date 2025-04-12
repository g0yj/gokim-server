package com.lms.api.common.repository;

import com.lms.api.common.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UserRepository extends JpaRepository<UserEntity, String>,
    QuerydslPredicateExecutor<UserEntity> {


}
