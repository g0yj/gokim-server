package com.lms.api.common.repository.community;

import com.lms.api.common.entity.community.CommunityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface CommunityRepository extends JpaRepository<CommunityEntity, String>,
    QuerydslPredicateExecutor<CommunityEntity> {



}
