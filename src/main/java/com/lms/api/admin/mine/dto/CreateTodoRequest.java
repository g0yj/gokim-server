package com.lms.api.admin.mine.dto;

import com.lms.api.admin.mine.enums.TodoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;


@Getter@Setter@Builder@AllArgsConstructor@NoArgsConstructor
@Schema(name = "CreateTodoRequest" , description = "Todo 생성 데이터" )
public class CreateTodoRequest {

    @Schema(description = "내용", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String title;

    @Schema(description = "todo 상태", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    TodoStatus todoStatus;

    @Schema(description = "todo 목록 순서", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    int sort;

    @Schema(description = "추가할 내용")
    String memo;

    @Schema(description = "시작한 날짜를 기록하고 싶을 때 사용합니다. null일 시 등록 시간으로 서버에서 처리합니다")
    LocalDate startDate;
    @Schema(description = "시작한 날짜를 기록하고 싶을 때 사용합니다. null일 시 등록 시간으로 서버에서 처리합니다")
    LocalDate endDate;

}
