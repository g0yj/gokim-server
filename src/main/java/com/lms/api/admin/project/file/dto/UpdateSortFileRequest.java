package com.lms.api.admin.project.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UpdateSortFileRequest", description = "파일 순서 변경할 때 사용합니다")
public class UpdateSortFileRequest {

    List<Change> sortOrders;

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Change {
        @Schema(description = "프로젝트 파일 식별키", requiredMode = Schema.RequiredMode.REQUIRED)
        String projectFileId;
        @Schema(description = "순서", requiredMode = Schema.RequiredMode.REQUIRED)
        int sortOrder;
    }
}
