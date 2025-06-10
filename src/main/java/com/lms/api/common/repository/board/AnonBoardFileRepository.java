package com.lms.api.common.repository.board;

import com.lms.api.common.entity.board.AnonBoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;



public interface AnonBoardFileRepository extends JpaRepository<AnonBoardFileEntity, Long>,
    QuerydslPredicateExecutor<AnonBoardFileEntity> {

}
