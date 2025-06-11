package com.lms.api.common.repository.board;


import com.lms.api.common.entity.board.CommunityBoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;



public interface CommunityBoardFileRepository extends JpaRepository<CommunityBoardFileEntity, Long>,
    QuerydslPredicateExecutor<CommunityBoardFileEntity> {

}
