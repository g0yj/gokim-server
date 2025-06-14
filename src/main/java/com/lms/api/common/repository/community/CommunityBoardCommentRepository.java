package com.lms.api.common.repository.community;


import com.lms.api.common.entity.community.CommunityBoardCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityBoardCommentRepository extends JpaRepository<CommunityBoardCommentEntity, Long>,
    QuerydslPredicateExecutor<CommunityBoardCommentEntity> {

}
