package com.lms.api.common.repository.board;

import com.lms.api.common.entity.board.NoticeEntity;
import com.lms.api.common.entity.board.NoticeFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;


public interface NoticeFileRepository extends JpaRepository<NoticeFileEntity, Long>,
    QuerydslPredicateExecutor<NoticeFileEntity> {

    List<NoticeFileEntity> findAllByNoticeEntity(NoticeEntity noticeEntity);
}
