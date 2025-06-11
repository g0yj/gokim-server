package com.lms.api.admin.board.community;

import com.lms.api.admin.board.community.dto.ListCommunity;
import com.lms.api.admin.board.community.dto.ListCommunityRequest;
import com.lms.api.admin.board.community.dto.ListCommunityResponse;
import com.lms.api.admin.board.community.dto.SearchCommunity;
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

    ListCommunityResponse toListCommunityResponse(ListCommunity source);
    default PageResponse<ListCommunityResponse> toListCommunityResponse(SearchCommunity searchCommunity, Page<ListCommunity> page){
        return toPageResponse(page, this::toListCommunityResponse, searchCommunity.getPageSize());
    }
}
