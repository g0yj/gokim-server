package com.lms.api.admin.board.anon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter@AllArgsConstructor@NoArgsConstructor@Builder
@Schema(name = "ListCommentResponse", description = "댓글 목록")
public class ListCommentResponse {
    @Schema(description = "댓글 식별키")
    Long id;
    @Schema(description = "댓글")
    String comment;
    @Schema(description = "최종 수정일")
    String modifiedOn;
    @Schema(description = "로그인 한 사람과 작성자가 동일한가")
    @JsonProperty("isMine")
    Boolean isMine;

}
