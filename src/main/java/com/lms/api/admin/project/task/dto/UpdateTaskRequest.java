package com.lms.api.admin.project.task.dto;

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

    @Schema(description = "제목")
    String title;
    @Schema(description = "설명")
    String description;
    @Schema(description = "담당자 식별키")
    String assignedMemberId;
    @Schema(description = "task 상태 식별키")
    long taskStatusId;

    @Schema(description = "새롭게 추가된 파일들")
    List<MultipartFile> files;
    @Schema(description = "삭제할 파일의 식별키들")
    List<Long> deleteFiles;

}
