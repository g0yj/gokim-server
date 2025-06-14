package com.lms.api.common.repository.community;


import com.lms.api.common.entity.community.CommunityBoardCommentEntity;
import com.lms.api.common.entity.community.CommunityBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityBoardCommentRepository extends JpaRepository<CommunityBoardCommentEntity, Long>,
    QuerydslPredicateExecutor<CommunityBoardCommentEntity> {

    List<CommunityBoardCommentEntity> findByCommunityBoardEntity(CommunityBoardEntity communityBoardEntity);
}
