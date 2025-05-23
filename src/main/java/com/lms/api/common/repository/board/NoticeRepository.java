package com.lms.api.common.repository.board;

import com.lms.api.common.entity.board.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface NoticeRepository extends JpaRepository<NoticeEntity, String>,
    QuerydslPredicateExecutor<NoticeEntity> {



}
