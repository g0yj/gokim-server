package com.lms.api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "PageRequest", description = "페이징 처리 관련")
public class PageRequest {

  @Schema(description = "페이징 처리 시 페이지 총 갯수")
  Integer page;
  @Schema(description = "한 페이지에 보여 줄 게시글 갯수")
  Integer limit;
  @Schema(description = "하단 바의 갯수")
  Integer pageSize;
  @Schema(description = "정렬 기준")
  String order;
  @Schema(description = "정렬 방법 (내림 desc, 오름 asc)")
  String direction; // asc, desc
  @Schema(description = "검색 조건에 해당 하는 키워드")
  String keyword;

  // search는 하위 클래스에서 생성하기로 함!!
}
