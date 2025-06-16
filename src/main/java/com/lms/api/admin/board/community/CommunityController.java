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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @PostMapping("/board/{communityId}")
    @Operation(summary = "커뮤니티 게시글 등록", description = "커뮤니티 상세 조회시 나오는 게시판 글 등록")
    public String createBoard(@LoginUser UserEntity userEntity,
                                         @Parameter(description = "커뮤니티 식별키") @PathVariable String communityId,
                                         @Valid @ModelAttribute CreateCommunityBoardRequest createCommunityBoardRequest){
        return communityService.createBoard(userEntity.getId(), createCommunityBoardRequest, communityId);
    }

    @GetMapping("/board/{communityId}")
    @Operation(summary = "커뮤니티 게시글 목록", description = "커뮤니티 상세 조회시 나오는 게시물 목록")
    public PageResponse<ListCommunityBoardResponse> listBoard(@Parameter(description = "커뮤니티 식별키")@PathVariable String communityId,
                                                              @ParameterObject ListCommunityBoardRequest listCommunityBoardRequest){
        SearchCommunityBoard searchCommunityBoard = communityControllerMapper.toSearchCommunityBoard(communityId,listCommunityBoardRequest);
        Page<ListCommunityBoard> page = communityService.listBoard(communityId,searchCommunityBoard);
        return communityControllerMapper.toListCommunityBoardResponse(searchCommunityBoard, page);
    }

    @PostMapping("/comment/{boardId}")
    @Operation(summary = "게시글 댓글 등록", description = "커뮤니티 게시글의 댓글")
    public Long createComment(@LoginUser UserEntity userEntity,
                                           @Parameter(description = "게시글 식별키") @PathVariable String boardId,
                                           @RequestBody @Valid CreateCommunityCommentRequest createCommunityCommentRequest){
        return communityService.createComment(userEntity.getId(), boardId, createCommunityCommentRequest);
    }

    @PostMapping("/reply/{commentId}")
    @Operation(summary = "대댓 등록" , description = "댓글에 대한 답글 등록")
    public Long createReply(@LoginUser UserEntity userEntity,
                              @Parameter(description = "댓글 식별키") @PathVariable Long commentId,
                              @RequestBody @Valid CreateCommunityReply createCommunityReply){
        return communityService.createReply(userEntity.getId(), commentId, createCommunityReply);
    }

    @GetMapping("/board/list/{boardId}")
    @Operation(summary = "게시글 상세조회")
    public GetBoard getBoard(@LoginUser UserEntity userEntity,
                             @Parameter(description = "게시글 식별키")@PathVariable String boardId){
       return communityService.getBoard(userEntity.getId(), boardId);
    }

    @GetMapping("/comment/{boardId}")
    @Operation(summary = "댓글 목록", description = "대댓을 포함해 전달")
    public List<ListCommunityBoardComment> listComment(@LoginUser UserEntity userEntity,
                                                       @Parameter(description = "게시글 식별키") @PathVariable  String boardId){
        return communityService.listComment(userEntity.getId(), boardId);
    }

    @PutMapping("/{commentId}/reply/{replyId}")
    @Operation(summary = "대댓 수정")
    public ResponseEntity<?> updateReply(@LoginUser UserEntity userEntity,
                                         @Parameter(description = "댓글 식별키") @PathVariable Long commentId,
                                         @Parameter(description = "대댓 식별키") @PathVariable Long replyId,
                                         @Valid @RequestBody UpdateCommunityReply updateCommunityReply){
        communityService.updateReply(userEntity.getId(), commentId, replyId, updateCommunityReply);
        return ResponseEntity.ok("대댓 수정 완료");
    }

    @DeleteMapping("/{commentId}/reply/{replyId}")
    @Operation(summary = "대댓 삭제")
    public ResponseEntity<?> deleteReply(@LoginUser UserEntity userEntity,
                                         @Parameter(description = "댓글 식별키") @PathVariable Long commentId,
                                         @Parameter(description = "대댓 식별키") @PathVariable Long replyId){
        communityService.deleteReply(userEntity.getId(), commentId, replyId);
        return ResponseEntity.ok("삭제 성공");
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "댓글 삭제" , description = "실제 댓글이 삭제되는건 아님. deleted로 업데이트 하는 방식으로 구현")
    public ResponseEntity<?> deleteComment(@LoginUser UserEntity userEntity,
                                           @Parameter(description = "댓글 식별키") @PathVariable Long commentId){
        communityService.deleteComment(userEntity.getId(), commentId);
        return ResponseEntity.ok("댓글삭제완료");
    }

    @PutMapping("/comment/{commentId}")
    @Operation(summary = "댓글 수정")
    public ResponseEntity<?> updateComment(@LoginUser UserEntity userEntity,
                                           @Parameter(description = "댓글 식별키") @PathVariable Long commentId,
                                           @RequestBody UpdateCommunityComment updateCommunityComment){
        communityService.updateComment(userEntity.getId(), commentId, updateCommunityComment);
        return ResponseEntity.ok("댓글 수정 성공");
    }

    @PutMapping("/board/{boardId}")
    @Operation(summary = "게시글 수정")
    public ResponseEntity<?> updateBoard(@LoginUser UserEntity userEntity,
                                         @Parameter(description = "게시글 식별키")@PathVariable String boardId,
                                         @Valid @ModelAttribute UpdateCommunityBoard updateCommunityBoard){
        communityService.updateBoard(userEntity.getId(), boardId, updateCommunityBoard);
        return ResponseEntity.ok("게시글 수정 성공");
    }

    @DeleteMapping("/board/{boardId}")
    @Operation(summary = "게시글 삭제")
    public ResponseEntity<?> deleteBoard(@LoginUser UserEntity userEntity,
                                         @Parameter(description = "게시글 식별키") @PathVariable String boardId){
        communityService.deleteBoard(userEntity.getId(), boardId);
        return ResponseEntity.ok("삭제 완료");
    }


}