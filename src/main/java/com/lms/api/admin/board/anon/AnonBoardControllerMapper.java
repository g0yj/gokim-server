package com.lms.api.admin.board.anon;


import com.lms.api.admin.board.anon.dto.*;
import com.lms.api.common.dto.PageResponse;
import com.lms.api.common.mapper.ControllerMapper;
import com.lms.api.common.mapper.ControllerMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;



@Mapper(componentModel = "spring", config = ControllerMapperConfig.class, uses = {
    ControllerMapper.class})
public interface AnonBoardControllerMapper extends ControllerMapper{


    @Mapping(target = "multipartFiles", source ="createAnonBoardRequest.files")
    @Mapping(target = "createdBy", source = "loginId")
    CreateAnonBoard toCreateAnonBoard(String loginId, CreateAnonBoardRequest createAnonBoardRequest);

    SearchAnonBoard toSearchAnonBoard(ListAnonBoardRequest request);

    ListAnonBoardResponse toListAnonBoardResponse(ListAnonBoard listAnonBoard);

    default PageResponse<ListAnonBoardResponse> toListAnonBoardResponse(
            SearchAnonBoard search,
            Page<ListAnonBoard> page
    ) {
        return toPageResponse(page, this::toListAnonBoardResponse, search.getPageSize());
    }
}
