package com.lms.api.admin.controller.dto.project.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Task 수정 데이터")
public class UpdateTaskRequest {

    @Schema(description = "목록 조회 시 출력 순서. todo 순서를 변경할 때마다 api 호출이 필요하며, 이때 사용합니다. 목록에 있는 모든 task의 sortOrder을 순서에 따라 변경하여 넘겨야합니다.")
    int sortOrder;
    @Schema(description = "제목")
    String title;
    @Schema(description = "설명")
    String description;
    @Schema(description = "담당자 식별키")
    String assignedMemberId;
    @Schema(description = "task 상태 식별키")
    long taskStatusId;


    List<MultipartFile> files; // 파일들
    List<Long> deleteFiles; // 삭제할 파일 목록




}
