package com.lms.api.admin.project.file.dto;

import com.lms.api.admin.project.enums.ProjectFunctionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "ListProjectFileResponse", description = "필터링된 파일 목록")
public class ListProjectFileResponse {

    @Schema(description = "파일 식별키")
    String projectFileId;

    @Schema(description = "파일 주소")
    String fileUrl;

    @Schema(description = "파일명")
    String originalFileName;

    @Schema(description = "확장자")
    String extension;

    @Schema(description = "작성자(멤버) 식별키")
    String projectMemberId;
    @Schema(description = "작성자(멤버) 이름")
    String projectMemberName;
    @Schema(description = "작성자(멤버) 이미지")
    String userImgUrl;

}
