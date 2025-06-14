package com.lms.api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PageResponseData {
  @Schema(description = "No -> 서버에서 자동 처리하도록 구현. 게시글 맨 앞에 게시글 숫자 필요할 때 사용")
  private long listNumber;
}
