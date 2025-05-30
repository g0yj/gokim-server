package com.lms.api.admin.mine.dto;

import com.lms.api.admin.mine.enums.TodoStatus;
import com.lms.api.admin.project.enums.ProjectRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter@Builder
@AllArgsConstructor@NoArgsConstructor
public class ListTodoResponse {
    @Schema(description = "Todo 상태 > READY-시작전 / IN_PROGRESS- 진행중 / DONE- 완료")
    TodoStatus todoStatus;
    List<Todo> todos;

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Todo {
        @Schema(description = "todo 식별키")
        Long id;
        @Schema(description = "todo 내용")
        String title;
        @Schema(description = "Todo 상태 > READY-시작전 / IN_PROGRESS- 진행중 / DONE- 완료")
        TodoStatus todoStatus;
        @Schema(description = "목록 순서")
        int sort;

    }
}
