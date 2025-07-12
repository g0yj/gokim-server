package com.lms.api.common.repository;

import com.lms.api.common.entity.ScrapEntity;
import com.lms.api.common.entity.community.CommunityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;


public interface ScrapRepository extends JpaRepository<ScrapEntity, String>,
    QuerydslPredicateExecutor<ScrapEntity> {

    Optional<ScrapEntity> findByTargetId(String communityId);
}
