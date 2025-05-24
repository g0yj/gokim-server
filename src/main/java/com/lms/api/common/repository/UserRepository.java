package com.lms.api.common.repository;

import com.lms.api.admin.auth.enums.LoginType;
import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.common.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String>,
    QuerydslPredicateExecutor<UserEntity> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmailAndLoginType(String email, LoginType provider);


    boolean existsByIdAndRole(String loginId, UserRole userRole);

    Optional<UserEntity> findByIdAndRole(String id, UserRole role);
}
