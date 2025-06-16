package com.lms.api.admin.board.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor@Builder
@Schema(name = "GetCommunity", description = "커뮤니티 상세조회")
public class GetCommunity {
    @Schema(description = "커뮤니티 식별키")
    String id;
    @Schema(description = "제목")
    String title;
    @Schema(description = "내용")
    String description;
    @Schema(description = "파일명")
    String originalFileName;
    @Schema(description = "파일 주소")
    String url;
    @Schema(description = "작성자 여부")
    @JsonProperty("isMine")
    Boolean isMine;
    @Schema(description = "최종 수정일")
    String modifiedOn;
    @Schema(description = "작성자")
    String createdBy;
}
