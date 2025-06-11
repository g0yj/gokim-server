package com.lms.api.common.repository.board;

import com.lms.api.common.entity.board.AnonBoardCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;




public interface AnonBoardCommentRepository extends JpaRepository<AnonBoardCommentEntity, Long>,
    QuerydslPredicateExecutor<AnonBoardCommentEntity> {

}
