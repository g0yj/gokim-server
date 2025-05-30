package com.lms.api.admin.mine.dto;

import com.lms.api.admin.mine.enums.TodoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name ="ChangeTodoRequest" , description = "순서 변경 시 전달 해야할 데이터")
public class ChangeTodoRequest {

    @Schema(description = "변경되는 todo의 식별키, 상태 변경이 있을때는 필수입니다")
    Long todoId;
    @Schema(description = "Todo 상태 > READY-시작전 / IN_PROGRESS- 진행중 / DONE- 완료")
    TodoStatus newStatus;
    @NotNull
    @Schema(description = "변경된 sort")
    List<Long> newOrderForStatus;
}
