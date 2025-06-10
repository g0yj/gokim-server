package com.lms.api.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "PageResponse", description = "페이징 처리 정보")
public class PageResponse<T> {

  @Schema(description = "결과 리스트")
  List<T> list;

  @Schema(description = "총 게시물 수")
  long totalCount;

  @Schema(description = "현재 페이지 번호")
  int page;

  @Schema(description = "한 페이지당 게시물 수")
  int limit;

  @Schema(description = "페이지 바 크기")
  int pageSize;

  public PageResponse(Page<T> page, int pageSize) {
    this.list = page.getContent();
    this.totalCount = page.getTotalElements();
    int currentPage = page.getNumber() + 1;
    this.page = currentPage;
    this.limit = page.getSize();
    this.pageSize = pageSize;

    if (list != null && !list.isEmpty() && list.get(0) instanceof PageResponseData) {
      for (int i = 0; i < list.size(); i++) {
        ((PageResponseData) list.get(i)).setListNumber(
                totalCount - (long) (currentPage - 1) * limit - i
        );
      }
    }
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
