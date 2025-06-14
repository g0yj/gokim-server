package com.lms.api.admin.board.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor@Builder
@Schema(name = "GetBoard" , description = "게시글 상세 조회")
public class GetBoard {
    @Schema(description = "게시글 식별키")
    String id;
    @Schema(description = "제목")
    String title;
    @Schema(description = "내용")
    String content;
    @Schema(description = "등록일")
    LocalDate createdOn;
    @Schema(description = "작성자")
    String createdBy;
    @Schema(description = "파일 정보")
    List<FileMeta> files;
    @Schema(description = "조회수")
    int view;
    @Schema(description = "작성자와 로그인한 사람이 동일한지 여부")
    @JsonProperty("isMine")
    Boolean isMine;


    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class FileMeta {
        @Schema(description = "파일 식별키")
        Long boardFileId;
        @Schema(description = "파일명")
        String originalFileName;
        @Schema(description = "다운로드 링크")
        String url;
    }
}
