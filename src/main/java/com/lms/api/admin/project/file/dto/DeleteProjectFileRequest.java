package com.lms.api.admin.project.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter@Getter@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "DeleteProjectFileRequest", description = "삭제할 파일의 식별키들 모음")
public class DeleteProjectFileRequest {
    @Schema(description = "파일 식별키")
    List<String> deleteFileIds;
}
