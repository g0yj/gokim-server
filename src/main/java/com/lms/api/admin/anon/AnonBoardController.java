package com.lms.api.admin.anon;


import com.lms.api.admin.anon.dto.CreateAnonBoard;
import com.lms.api.admin.anon.dto.CreateAnonBoardRequest;
import com.lms.api.admin.auth.LoginUser;
import com.lms.api.common.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;



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

}
