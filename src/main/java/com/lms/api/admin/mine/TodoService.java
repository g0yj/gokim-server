package com.lms.api.admin.mine;


import com.lms.api.admin.mine.dto.CreateTodoRequest;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.mine.TodoEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.common.repository.mine.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoServiceMapper todoServiceMapper;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createTodo(String userId, CreateTodoRequest createTodoRequest) {
        LocalDate now = LocalDate.now();

        LocalDate startDate = createTodoRequest.getStartDate() != null ? createTodoRequest.getStartDate(): now;
        LocalDate endDate = createTodoRequest.getEndDate() != null ? createTodoRequest.getEndDate(): now;

        if(endDate.isBefore(startDate)){
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        TodoEntity todoEntity = TodoEntity.builder()
                .title(createTodoRequest.getTitle())
                .todoStatus(createTodoRequest.getTodoStatus())
                .sort(createTodoRequest.getSort())
                .memo(createTodoRequest.getMemo())
                .startDate(startDate)
                .endDate(endDate)
                .color(null)
                .createdBy(userId)
                .modifiedBy(userId)
                .userEntity(userEntity)
                .build();

        todoEntity = todoRepository.save(todoEntity);
        return todoEntity.getId();
    }
}



