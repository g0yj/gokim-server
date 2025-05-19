package com.lms.api.admin.user;

import com.lms.api.admin.user.dto.CreateUser;
import com.lms.api.admin.user.dto.CreateUserRequest;
import com.lms.api.admin.user.dto.CreateUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/*
    @GetMapping
    @Operation(summary = "프로젝트 목록" , description = "로그인한 회원이 참여 중인 프로젝트만 조회됩니다")
    public List<ListProjectResponse> listProject(@LoginUser UserEntity user){
        List<Project> projects = projectService.listProject(user.getId());
        return projectControllerMapper.toListProjectResponse(projects);
    }

    @PutMapping("/{id}")
    @Operation(summary = "프로젝트 수정" , description = "프로젝트 변경 시 사용합니다.")
    public ResponseEntity<?> updateProject(@LoginUser UserEntity user, @PathVariable String id, @Valid @RequestBody UpdateProjectRequest updateProjectRequest){
        projectService.updateProject(user.getId(), id, updateProjectRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "프로젝트 삭제", description = "권한이 소유자인 경우만 삭제가 가능하도록 접근 제한 합니다")
    public ResponseEntity<?> deleteProject(@LoginUser UserEntity userEntity, @PathVariable String id){
        projectService.deleteProject(userEntity.getId(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "프로젝트 기능 목록", description = "프로젝트가 가진 기능에 대한 식별키 입니다. 캘린더, 보드 등등.. 식별키를 통해 프로젝트가 가진 기능을 조회")
    public ProjectFunction listProjectFunction(@PathVariable String id){
        return projectService.projectFunction(id);
    }

*/

}
