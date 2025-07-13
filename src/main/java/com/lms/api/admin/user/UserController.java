package com.lms.api.admin.user;

import com.lms.api.admin.auth.LoginUser;
import com.lms.api.admin.user.dto.*;
import com.lms.api.common.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@Tag(name = "User API", description = "회원 관련 API입니다")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserControllerMapper userControllerMapper;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "회원가입", description = "일반 회원가입입니다. 소셜 회원가입은 소셜로그인 시 자동 가입 됩니다")
    public CreateUserResponse createUser(@Valid CreateUserRequest createUserRequest){
        CreateUser createUser = userControllerMapper.toCreateUser(createUserRequest);
        return userService.createUser(createUser);
    }
    @GetMapping
    @Operation(summary = "회원 상세 조회")
    public GetUser getUser(@LoginUser UserEntity user){
        return userService.getUser(user.getId());
    }

    @PutMapping
    @Operation(summary = "회원 수정")
    public ResponseEntity<?> updateUser(@LoginUser UserEntity user,  @Valid UpdateUserRequest updateUserRequest){
        UpdateUser updateUser = userControllerMapper.toUpdateUser(user.getId(), updateUserRequest);
        userService.updateUser(user.getId(),updateUser);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping
    @Operation(summary = "회원 탈퇴")
    public ResponseEntity<?> deleteUser(@LoginUser UserEntity userEntity){
        userService.deleteUser(userEntity.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "회원 찾기")
    public String searchUser(@Parameter(description = "회원아이디") @RequestParam String id){
        userService.searchUser(id);
        return id;
    }


}
