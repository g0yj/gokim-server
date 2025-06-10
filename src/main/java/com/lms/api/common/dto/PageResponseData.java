package com.lms.api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PageResponseData {
  @Schema(description = "no")
  private long listNumber;
}
