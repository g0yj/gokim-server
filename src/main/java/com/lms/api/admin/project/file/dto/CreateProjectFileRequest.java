package com.lms.api.admin.project.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter@Getter@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "CreateProjectFileRequest" , description = "파일 업로드 기능이 있는 카테고리에서 새로 생성 시 사용")
public class CreateProjectFileRequest {
    @Schema(description = "추가할 첨부 파일", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    List<MultipartFile> files;
}
