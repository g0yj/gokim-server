package com.lms.api.admin.mine;


import com.lms.api.admin.mine.dto.CreateTodoRequest;
import com.lms.api.admin.mine.dto.ListTodoResponse;
import com.lms.api.admin.mine.enums.TodoStatus;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.mine.QTodoEntity;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    private final JpaConfig jpaConfig;
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

    @Transactional
    public List<ListTodoResponse> listTodo(String userId) {
        QTodoEntity qTodoEntity = QTodoEntity.todoEntity;

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        List<TodoEntity> allTodos  = jpaConfig.queryFactory()
                .select(qTodoEntity)
                .from(qTodoEntity)
                .where(qTodoEntity.userEntity.id.eq(userId))
                .orderBy(qTodoEntity.sort.asc())
                .fetch();

        Map<TodoStatus, List<TodoEntity>> groupedTodos = allTodos.stream()
                .collect(Collectors.groupingBy(TodoEntity::getTodoStatus));

        List<ListTodoResponse> result = groupedTodos.entrySet().stream()
                .map(entry -> {
                    TodoStatus status = entry.getKey();
                    List<ListTodoResponse.Todo> todos = todoServiceMapper.toTodo(entry.getValue());
                    return ListTodoResponse.builder()
                            .todoStatus(status)
                            .todos(todos)
                            .build();
                })
                .toList();

        return result;
    }
}



