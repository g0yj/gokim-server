package com.lms.api.admin.board.notice;


import com.lms.api.admin.auth.LoginUser;
import com.lms.api.admin.board.dto.CreateNotice;
import com.lms.api.admin.board.dto.CreateNoticeRequest;
import com.lms.api.admin.board.notice.dto.*;
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



@RestController
@RequestMapping("/notice")
@Tag(name = "공지사항 API")
@RequiredArgsConstructor
@Slf4j
public class NoticeController {
    private final NoticeControllerMapper noticeControllerMapper;
    private final NoticeService noticeService;

    @PostMapping
    @Operation( summary = "공지사항 등록")
    public String createNotice(@LoginUser UserEntity userEntity, @Valid @ModelAttribute CreateNoticeRequest createNoticeRequest){
        CreateNotice createNotice = noticeControllerMapper.toCreateNotice(userEntity.getId(), createNoticeRequest);
        return noticeService.createNotice(userEntity.getId(), createNotice);
    }

    @GetMapping("/page")
    @Operation(summary = "공지사항 목록", description = "필터링을 포함합니다")
    public Page<ListPageNoticeResponse> pageListNotice(@LoginUser UserEntity userEntity, @Valid @ParameterObject @RequestBody ListPageNoticeRequest listPageNoticeRequest){
        SearchNotice searchNotice = noticeControllerMapper.toSearchNotice(userEntity.getId(), listPageNoticeRequest);
        return  noticeService.pageListNotice(userEntity.getId(), searchNotice);
    }

    @GetMapping("/{id}")
    @Operation(summary = "공지사항 상세 조회")
    public GetNoticeResponse getNotice( @LoginUser UserEntity userEntity,
                                        @Parameter(description = "공지사항 식별키") @PathVariable String id){
        return noticeService.getNotice(userEntity.getId(), id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "공지사항 수정")
    public ResponseEntity<?> updateNotice(@LoginUser UserEntity userEntity,
                                          @Parameter(description = "공지사항 식별키")@PathVariable String id,
                                          @Valid @ParameterObject @ModelAttribute UpdateNoticeRequest updateNoticeRequest){
        noticeService.updateNotice(userEntity.getId(), id, updateNoticeRequest);
        return ResponseEntity.ok("ok");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "공지사항 삭제")
    public ResponseEntity<?> deleteNotice(@LoginUser UserEntity userEntity, @Parameter(description = "공지사항 식별키") @PathVariable String id){
        noticeService.deleteNotice(userEntity.getId(), id);
        return ResponseEntity.ok("ok");
    }

}
