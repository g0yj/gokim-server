package com.lms.api.admin.board.anon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "GetAnonBoard", description = "익명 게시글 상세 조회")
public class GetAnonBoard {
    @Schema(description = "익명 게시글 식별키")
    String id;
    @Schema(description = "제목")
    String title;
    @Schema(description = "본문")
    String content;
    @Schema(description = "조회수")
    int view;
    @Schema(description = "게시글 작성자 (수정, 삭제 등 로그인한 사람과 작성자와 비교를 위해 사용)")
    String createdBy;
    @Schema(description = "파일 정보 ")
    List<AnonBoardFile> files;

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AnonBoardFile {
        @Schema(description = "파일 식별키")
        Long anonBoardFileId;
        @Schema(description = "파일명")
        String originalFileName;
        @Schema(description = "다운로드 링크")
        String url;
    }

}
