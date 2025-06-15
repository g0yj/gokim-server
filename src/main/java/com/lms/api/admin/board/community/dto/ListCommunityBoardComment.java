package com.lms.api.admin.board.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor@Builder
@Schema(name ="ListCommunityBoardComment", description = "댓글 + 대댓글")
public class ListCommunityBoardComment {
    @Schema(description = "게시글 식별키")
    String boardId;

    @Schema(description = "댓글 식별키")
    Long id;
    @Schema(description = "댓글")
    String comment;
    @Schema(description = "최종 수정일")
    String modifiedOn;
    @Schema(description = "댓글 작성자")
    String modifiedBy;
    @Schema(description = "댓글 수정이 가능한지 여부")
    @JsonProperty("commentMine")
    Boolean commentMine;
    @Schema(description = "댓글 공개 여부")
    @JsonProperty("isSecret")
    Boolean isSecret;
    @Schema(description = "대댓글 목록")
    List<Reply> replies;

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Reply {
        @Schema(description = "대댓 식별키")
        Long replyId;
        @Schema(description = "대댓 내용")
        String reply;
        @Schema(description = "최종 수정일")
        String modifiedOn;
        @Schema(description = "대댓 작성자")
        String modifiedBy;
        @Schema(description = "대댓 수정이 가능한지 여부")
        @JsonProperty("replyMine")
        Boolean replyMine;
        @Schema(description = "대댓 공개 여부")
        @JsonProperty("isSecret")
        Boolean isSecret;
    }
}
