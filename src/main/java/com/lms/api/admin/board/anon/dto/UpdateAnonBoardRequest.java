package com.lms.api.admin.board.anon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter@NoArgsConstructor@AllArgsConstructor@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UpdateAnonBoardRequest", description = " 익명 게시글 수정 데이터")
public class UpdateAnonBoardRequest {
    @Schema(description = "익명 게시글 제목")
    String title;
    @Schema(description = "익명 게시글 본문")
    String content;

    @Schema(description = "추가할 파일")
    List<MultipartFile> files;

    @Schema(description = "삭제할 파일의 식별키")
    List<Long> deleteFileIds;

    @Schema(description = "수정한 사람으로 서버에서 처리하니 프론트에선 전달하지 않아도 됨")
    String modifiedBy;
}
