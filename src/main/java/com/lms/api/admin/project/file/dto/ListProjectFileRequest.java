package com.lms.api.admin.project.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "ListProjectFileRequest", description = "파일 조회 시 조건. 필터링 기능이 가능합니다")
public class ListProjectFileRequest  {

    @Schema(description = "검색어. 파일명을 기준으로 필터링 합니다")
    String keyword;

    @Schema(description = "멤버 식별키. 작성자에 따라 필터링 합니다")
    String projectMemberId;

    @Schema(description = "확장자에 따라 필터링 합니다")
    String extension;

}
