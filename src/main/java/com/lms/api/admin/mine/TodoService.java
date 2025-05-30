package com.lms.api.admin.mine;


import com.lms.api.admin.mine.dto.ChangeTodoRequest;
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
import java.util.Comparator;
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

        LocalDate startDate = createTodoRequest.getStartDate() != null ? createTodoRequest.getStartDate() : now;
        LocalDate endDate = createTodoRequest.getEndDate() != null ? createTodoRequest.getEndDate() : now;

        if (endDate.isBefore(startDate)) {
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

        List<TodoEntity> allTodos = jpaConfig.queryFactory()
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

    @Transactional
    public void changeTodo(String userId, ChangeTodoRequest changeTodoRequest) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        if (changeTodoRequest.getTodoId() != null && changeTodoRequest.getNewStatus() != null) {
            TodoEntity todoEntity = todoRepository.findById(changeTodoRequest.getTodoId())
                    .orElseThrow(() -> new ApiException(ApiErrorCode.TODO_NOT_FOUND));
            todoEntity.setTodoStatus(changeTodoRequest.getNewStatus());
        }

        List<Long> newOrder = changeTodoRequest.getNewOrderForStatus();

        // 1. To-do 리스트 조회 (id 순서대로 정렬된 리스트 조회)
        List<TodoEntity> todos = todoRepository.findAllById(newOrder)
                .stream()
                // newOrder 순서대로 정렬
                .sorted(Comparator.comparingInt(id -> newOrder.indexOf(id.getId())))
                .collect(Collectors.toList());

        // 2. 간격 체크 함수
        boolean needsReorder = false;
        for (int i = 0; i < todos.size() - 1; i++) {
            int currentSort = todos.get(i).getSort();
            int nextSort = todos.get(i + 1).getSort();
            if (nextSort - currentSort <= 1) {
                needsReorder = true;
                break;
            }
        }

        // 3. 간격 부족하거나 재배치 필요하면 전체 재배치
        if (needsReorder) {
            int sortValue = 10;
            int increment = 10;
            for (TodoEntity todo : todos) {
                todo.setSort(sortValue);
                sortValue += increment;
            }
        } else {
            // 4. 간격 충분하면 변경된 순서대로 sort값만 재할당 (기존 간격 유지)
            // 단순히 기존 sort값을 유지하면 안되고, 새 순서에 맞게 sort값도 변경해줘야 하므로 아래처럼 처리 가능
            int sortValue = 10;
            int increment = 10;
            for (TodoEntity todo : todos) {
                todo.setSort(sortValue);
                sortValue += increment;
            }
        }
    }
}



