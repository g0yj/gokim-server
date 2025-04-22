package com.lms.api.admin.controller.dto.project.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "이슈 만들기 (todo 생성 데이터)")
public class CreateTaskRequest {

    @Schema(description = "프로젝트 식별키")
    @NotBlank(message = "필수 값")
    String projectId;

    @Schema(description = "TODO 제목")
    @NotBlank(message = "필수 값")
    String title;

    @Schema(description = "이슈 만들기 제목")
    @NotNull(message = "status식별키로, 검토중, 완료와 같은 상태의 식별키 필수")
    Long taskStatusId;


}
