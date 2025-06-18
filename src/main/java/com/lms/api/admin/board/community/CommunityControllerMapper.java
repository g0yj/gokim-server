package com.lms.api.admin.board.community;

import com.lms.api.admin.board.community.dto.*;
import com.lms.api.common.dto.PageResponse;
import com.lms.api.common.mapper.ControllerMapper;
import com.lms.api.common.mapper.ControllerMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;


@Mapper(componentModel = "spring", config = ControllerMapperConfig.class, uses = {
    ControllerMapper.class})
public interface CommunityControllerMapper extends ControllerMapper{

    SearchCommunity toSearchCommunity(ListCommunityRequest listCommunityRequest);

    @Mapping(target = "boardId", source = "boardId")
    ListCommunityResponse toListCommunityResponse(ListCommunity source);
    default PageResponse<ListCommunityResponse> toListCommunityResponse(SearchCommunity searchCommunity, Page<ListCommunity> page){
        return toPageResponse(page, this::toListCommunityResponse, searchCommunity.getPageSize());
    }

    SearchCommunityBoard toSearchCommunityBoard(String communityId, ListCommunityBoardRequest listCommunityBoardRequest);


    default PageResponse<ListCommunityBoardResponse> toListCommunityBoardResponse(SearchCommunityBoard searchCommunityBoard, Page<ListCommunityBoard> page){
        return toPageResponse(page, this::toListCommunityBoardResponse, searchCommunityBoard.getPageSize());
    };
    ListCommunityBoardResponse toListCommunityBoardResponse(ListCommunityBoard listCommunityBoard);
}
