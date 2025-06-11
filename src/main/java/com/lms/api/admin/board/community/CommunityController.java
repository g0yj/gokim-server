package com.lms.api.admin.board.community;



import com.lms.api.admin.auth.LoginUser;
import com.lms.api.admin.board.community.dto.CreateCommunityRequest;
import com.lms.api.common.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

}
