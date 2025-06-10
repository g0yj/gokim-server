package com.lms.api.admin.board.anon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @Schema(description = "작성자인지 아닌지 (수정,삭제버튼 노출을 위해 사용")
    @JsonProperty("isMine") // 안 붙이면 mine으로 변경되어 swagger에 표기되며, @Schema가 안나옴
    boolean isMine;
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
