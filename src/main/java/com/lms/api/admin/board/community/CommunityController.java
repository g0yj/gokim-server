package com.lms.api.admin.board.community;



import com.lms.api.admin.auth.LoginUser;
import com.lms.api.admin.board.community.dto.*;
import com.lms.api.common.dto.PageResponse;
import com.lms.api.common.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/community")
@Tag(name = "커뮤니티 API")
@RequiredArgsConstructor
@Slf4j
public class CommunityController {
    private final CommunityService communityService;
    private final CommunityControllerMapper communityControllerMapper;

    @PostMapping
    @Operation(summary = "커뮤니티 생성")
    public String createCommunity(@LoginUser UserEntity userEntity,
                                  @Valid CreateCommunityRequest createCommunityRequest){
        return  communityService.createCommunity(userEntity.getId(), createCommunityRequest);
    }

    @GetMapping
    @Operation(summary = "커뮤니티 목록")
    public PageResponse<ListCommunityResponse> listCommunity(@Valid @ParameterObject ListCommunityRequest listCommunityRequest){
        SearchCommunity searchCommunity = communityControllerMapper.toSearchCommunity(listCommunityRequest);
        Page<ListCommunity> page = communityService.listCommunity(searchCommunity);
        return communityControllerMapper.toListCommunityResponse(searchCommunity, page);
    }

    @PostMapping("/{communityId}/board")
    @Operation(summary = "커뮤니티 게시글 등록", description = "커뮤니티 상세 조회시 나오는 게시판 글 등록")
    public String createBoard(@LoginUser UserEntity userEntity,
                                         @Parameter(description = "커뮤니티 식별키") @PathVariable String communityId,
                                         @Valid @ModelAttribute CreateCommunityBoardRequest createCommunityBoardRequest){
        return communityService.createBoard(userEntity.getId(), createCommunityBoardRequest, communityId);
    }

    @GetMapping("/{communityId}/board")
    @Operation(summary = "커뮤니티 게시글 목록", description = "커뮤니티 상세 조회시 나오는 게시물 목록")
    public PageResponse<ListCommunityBoardResponse> listBoard(@Parameter(description = "커뮤니티 식별키")@PathVariable String communityId,
                                                              @ParameterObject ListCommunityBoardRequest listCommunityBoardRequest){
        SearchCommunityBoard searchCommunityBoard = communityControllerMapper.toSearchCommunityBoard(communityId,listCommunityBoardRequest);
        Page<ListCommunityBoard> page = communityService.listBoard(communityId,searchCommunityBoard);
        return communityControllerMapper.toListCommunityBoardResponse(searchCommunityBoard, page);
    }

}
