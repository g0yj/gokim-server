package com.lms.api.admin.mine.dto;

import com.lms.api.admin.mine.enums.TodoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter@Setter@Builder
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateTodoRequest {

    @Schema(description = "내용", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String title;
    @Schema(description = "todo 상태", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    TodoStatus todoStatus;
    @Schema(description = "todo 상세 조회 시 추가로 넣고 싶은 내용이 있을 때 사용")
    String memo;
    @Schema(description = "todo 시작일")
    LocalDate startDate;
    @Schema(description = "todo 종료일")
    LocalDate endDate;
    @Schema(description = "색으로 구분 하고 싶을 때를 대비해 추가한 필드")
    String color;
}
