package com.lms.api.admin.board.notice;


import com.lms.api.admin.auth.LoginUser;
import com.lms.api.admin.board.dto.CreateNotice;
import com.lms.api.admin.board.dto.CreateNoticeRequest;
import com.lms.api.common.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
