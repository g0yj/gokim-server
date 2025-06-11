package com.lms.api.admin.board.anon;


import com.lms.api.admin.auth.LoginUser;
import com.lms.api.admin.board.anon.dto.*;
import com.lms.api.common.dto.PageResponse;
import com.lms.api.common.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/anon")
@Tag(name = "익명 게시판 API")
@RequiredArgsConstructor
@Slf4j
public class AnonBoardController {
    private final AnonBoardControllerMapper anonBoardControllerMapper;
    private final AnonBoardService anonBoardService;

    @PostMapping
    @Operation( summary = "익명 게시판 등록")
    public String createAnonBoard(@LoginUser UserEntity userEntity, @ModelAttribute @Valid CreateAnonBoardRequest createAnonBoardRequest){
        CreateAnonBoard createAnonBoard = anonBoardControllerMapper.toCreateAnonBoard (userEntity.getId(), createAnonBoardRequest);
        return anonBoardService.createAnonBoard(userEntity.getId(), createAnonBoard);
    }

    @GetMapping
    @Operation( summary = "익명 게시판 목록")
    public PageResponse<ListAnonBoardResponse> listAnonBoard(@Valid ListAnonBoardRequest listAnonBoardRequest) {
        SearchAnonBoard searchAnonBoard = anonBoardControllerMapper.toSearchAnonBoard(listAnonBoardRequest);
        Page<ListAnonBoard> page = anonBoardService.listAnonBoard(searchAnonBoard);
        return anonBoardControllerMapper.toListAnonBoardResponse(searchAnonBoard, page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "익명 게시판 상세 조회")
    public GetAnonBoard getAnonBoard(@LoginUser UserEntity userEntity, @PathVariable String id){
        return anonBoardService.getAnonBoard(userEntity.getId(), id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "익명 게시판 수정")
    public ResponseEntity<?> updateAnonBoard(
            @LoginUser UserEntity userEntity,
            @Parameter(description = "게시글 식별키") @PathVariable String id,
            @ModelAttribute @Valid UpdateAnonBoardRequest updateAnonBoardRequest){
        anonBoardService.updateAnonBoard(id, userEntity.getId(), updateAnonBoardRequest);
        return ResponseEntity.ok("수정완료");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "익명 게시글 삭제")
    public ResponseEntity<?> deleteAnonBoard(
            @LoginUser UserEntity userEntity,
            @Parameter(description = "게시글 식별키") @PathVariable String id) {
        anonBoardService.deleteAnonBoard(userEntity.getId(), id);
        return ResponseEntity.ok("삭제완료");
    }

    @PostMapping("/{boardId}/comment")
    @Operation(summary = "댓글 등록")
    public ResponseEntity<?> createComment(@LoginUser UserEntity userEntity,
                                           @Parameter(description = "익명 게시글 식별키")@PathVariable String boardId,
                                           @Valid @RequestBody CreateBoardCommentRequest createBoardCommentRequest) {
        anonBoardService.createComment(userEntity.getId(), boardId, createBoardCommentRequest);
        return ResponseEntity.ok("댓글 등록 성공");
    }

    @GetMapping("/{boardId}/comment")
    @Operation(summary = "댓글 목록")
    public List<ListCommentResponse> listComment(@LoginUser UserEntity userEntity,
                                                 @Parameter(description = "익명 게시글 식별키") @PathVariable String boardId){
        return anonBoardService.listComment(userEntity.getId(),boardId);
    }

    @PutMapping("/comment/{commentId}")
    @Operation(summary = "댓글 수정")
    public ResponseEntity<?> updateComment(@LoginUser UserEntity userEntity,
                                           @Parameter(description = "익명 게시글 식별키") @PathVariable Long commentId,
                                           @Valid @RequestBody UpdateBoardCommentRequest updateBoardCommentRequest){
        anonBoardService.updateComment(userEntity.getId(), commentId, updateBoardCommentRequest);
        return ResponseEntity.ok("수정완료");
    }

}
