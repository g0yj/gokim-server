package com.lms.api.common.repository.board;

import com.lms.api.common.entity.board.AnonBoardCommentEntity;
import com.lms.api.common.entity.board.AnonBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;


public interface AnonBoardCommentRepository extends JpaRepository<AnonBoardCommentEntity, Long>,
    QuerydslPredicateExecutor<AnonBoardCommentEntity> {

    List<AnonBoardCommentEntity> findAllByAnonBoardEntity(AnonBoardEntity anonBoardEntity);
}
