package com.lms.api.admin.mine;

import com.lms.api.admin.auth.LoginUser;
import com.lms.api.admin.mine.dto.CreateTodoRequest;
import com.lms.api.admin.mine.dto.ListTodoResponse;
import com.lms.api.common.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/todo")
@Tag(name = "Todo API", description = "Todo 관련 API 입니다")
@RequiredArgsConstructor
@Slf4j
public class TodoController {
    private final TodoControllerMapper todoControllerMapper;
    private final TodoService todoService;

    @PostMapping
    @Operation(summary = "TODO 생성", description = "새로운 Todo를 생성합니다")
    public Long createTodo(@LoginUser UserEntity userEntity, @Valid @RequestBody CreateTodoRequest createTodoRequest){
        return todoService.createTodo(userEntity.getId(), createTodoRequest);
    }

    @GetMapping
    @Operation(summary = "TODO 목록 조회" , description = "목록 조회로 상태 별로 그룹핑 해서 반환했습니다")
    public List<ListTodoResponse> listTodo(@LoginUser UserEntity userEntity) {
        return todoService.listTodo(userEntity.getId());
    }

}
