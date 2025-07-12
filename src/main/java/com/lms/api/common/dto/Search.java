package com.lms.api.common.dto;

import com.lms.api.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Getter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Search {

  @Builder.Default
  int page = 1;

  @Builder.Default
  int limit = 5;

  @Builder.Default
  int pageSize = 10;

  @Builder.Default
  String order = "createdOn";

  @Builder.Default
  String direction = "desc"; // asc, desc

  @Builder.Default
  String search = null;

  @Builder.Default
  String keyword = null;

  // ✅ Spring Data용 페이지 요청
  public PageRequest toPageRequest() {
    return PageRequest.of(page - 1, limit, Sort.Direction.valueOf(direction.toUpperCase()), order);
  }

  // ✅ QueryDSL에서 직접 offset 사용
  public int getOffset() {
    return (page - 1) * limit;
  }
  public boolean hasSearch() {
    return StringUtils.hasAllText(search, keyword);
  }
}
