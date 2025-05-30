package com.lms.api.common.repository.mine;


import com.lms.api.common.entity.mine.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long>,
    QuerydslPredicateExecutor<TodoEntity> {

}
