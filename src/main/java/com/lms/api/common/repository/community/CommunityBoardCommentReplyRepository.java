package com.lms.api.common.repository.community;


import com.lms.api.common.entity.community.CommunityBoardReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityBoardCommentReplyRepository extends JpaRepository<CommunityBoardReplyEntity, Long>,
    QuerydslPredicateExecutor<CommunityBoardReplyEntity> {

}
