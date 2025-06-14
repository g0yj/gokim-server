package com.lms.api.common.repository.community;


import com.lms.api.common.entity.community.CommunityBoardCommentEntity;
import com.lms.api.common.entity.community.CommunityBoardReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityBoardReplyRepository extends JpaRepository<CommunityBoardReplyEntity, Long>,
    QuerydslPredicateExecutor<CommunityBoardReplyEntity> {


    List<CommunityBoardReplyEntity> findByCommunityBoardCommentEntity(CommunityBoardCommentEntity commentEntity);
}
