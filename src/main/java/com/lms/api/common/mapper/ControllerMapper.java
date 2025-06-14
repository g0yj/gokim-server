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

    /**
     * Page<D> -> PageResponse<R> 공통 변환 템플릿
     *
     * @param entityPage 변환 전 Page 객체
     * @param converter D -> R으로 매핑하는 함수
     * @param pageSize 페이지 네비게이션 바에 몇 개씩 보여줄지
     * @return PageResponse<R>
     */
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