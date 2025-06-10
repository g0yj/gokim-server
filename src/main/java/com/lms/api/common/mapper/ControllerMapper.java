package com.lms.api.common.mapper;

import com.lms.api.common.dto.PageResponse;
import com.lms.api.common.dto.PageResponseData;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.function.Function;

@Mapper(componentModel = "spring", config = ControllerMapperConfig.class)
public interface ControllerMapper {
    // ✅ 공통 Page → PageResponse 변환 로직
    default <D extends PageResponseData, R> PageResponse<R> toPageResponse(
            Page<D> entityPage,
            Function<D, R> converter,
            int pageSize
    ) {
        List<R> content = entityPage.getContent().stream()
                .map(converter)
                .toList();

        Page<R> mappedPage = new PageImpl<>(content, entityPage.getPageable(), entityPage.getTotalElements());

        return new PageResponse<>(mappedPage, pageSize);
    }

}