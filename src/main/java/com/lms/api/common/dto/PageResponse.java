package com.lms.api.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "PageResponse", description = "페이징 처리 정보")
public class PageResponse<T> {

  @Schema(description = "페이징 처리에 따른 no 값")
  List<T> list;
  @Schema(description = "총 게시물 수")
  long totalCount;
  @Schema(description = "현재 페이지")
  int page;
  @Schema(description = "한 페이지에서 보여줄 게시글 수")
  int limit;
  @Schema(description = "페이지 바 갯수")
  int pageSize;

  public List<T> getList() {
    if (list == null) {
      return List.of();
    }

    if (list.isEmpty() || !(list.get(0) instanceof PageResponseData)) {
      return list;
    }

    for (int i = 0; i < list.size(); i++) {
      ((PageResponseData) list.get(i)).setListNumber(totalCount - (long) (page - 1) * limit - i);
    }

    return list;
  }

  public int getTotalPage() {
    return (int) Math.ceil((double) totalCount / limit);
  }

  public int getStartPage() {
    return ((page - 1) / pageSize) * pageSize + 1;
  }

  public int getEndPage() {
    return Math.min(getStartPage() + pageSize - 1, getTotalPage());
  }

  @JsonProperty("hasPrev")
  public boolean hasPrev() {
    return getStartPage() > 1;
  }

  @JsonProperty("hasNext")
  public boolean hasNext() {
    return getEndPage() < getTotalPage();
  }

  @JsonProperty("isFirst")
  public boolean isFirst() {
    return getStartPage() == 1;
  }

  @JsonProperty("isLast")
  public boolean isLast() {
    return getEndPage() == getTotalPage();
  }
}
