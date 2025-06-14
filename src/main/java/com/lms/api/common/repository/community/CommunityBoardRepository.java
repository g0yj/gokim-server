package com.lms.api.common.repository.community;

import com.lms.api.common.entity.community.CommunityBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface CommunityBoardRepository extends JpaRepository<CommunityBoardEntity, String>,
    QuerydslPredicateExecutor<CommunityBoardEntity> {



}
