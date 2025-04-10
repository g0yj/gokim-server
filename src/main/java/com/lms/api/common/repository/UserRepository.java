package com.lms.api.common.repository;

import com.lms.api.common.dto.UserType;
import com.lms.api.common.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UserRepository extends JpaRepository<UserEntity, String>,
    QuerydslPredicateExecutor<UserEntity> {


}
