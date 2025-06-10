package com.lms.api.common.repository.board;

import com.lms.api.common.entity.board.AnonBoardEntity;
import com.lms.api.common.entity.board.AnonBoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;


public interface AnonBoardFileRepository extends JpaRepository<AnonBoardFileEntity, Long>,
    QuerydslPredicateExecutor<AnonBoardFileEntity> {

    List<AnonBoardFileEntity> findAllByAnonBoardEntity(AnonBoardEntity anonBoardEntity);
}
