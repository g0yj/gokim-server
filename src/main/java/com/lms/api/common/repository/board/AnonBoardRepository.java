package com.lms.api.common.repository.board;

import com.lms.api.common.entity.board.AnonBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface AnonBoardRepository extends JpaRepository<AnonBoardEntity, String>,
    QuerydslPredicateExecutor<AnonBoardEntity> {



}
