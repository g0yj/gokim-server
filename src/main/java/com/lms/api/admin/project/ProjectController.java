package com.lms.api.admin.project;

import com.lms.api.admin.project.dto.*;
import com.lms.api.admin.auth.LoginUser;
import com.lms.api.common.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/project")
@Tag(name = "Project API", description = "프로젝트 관련 API입니다")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {
    private final ProjectControllerMapper projectControllerMapper;
    private final ProjectService projectService;

    @GetMapping("/function")
    @Operation(summary = "기능 목록" , description = "프로젝트 생성 시, 기능을 추가할 때 추가할 기능에 대한 정보를 제공합니다. select 박스 등에 사용합니다")
    public List<FunctionResponse> listFunction(){
         return projectService.listFunction();
    }

    @PostMapping
    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성합니다.")
    public ResponseEntity<CreateProjectResponse> createProject(@LoginUser UserEntity user, @Valid @RequestBody CreateProjectRequest createProjectRequest){
        String projectId = projectService.createProject(user, createProjectRequest);
        return ResponseEntity.ok(new CreateProjectResponse(projectId));
    }

    @GetMapping
    @Operation(summary = "프로젝트 목록" , description = "로그인한 회원이 참여 중인 프로젝트만 조회됩니다")
    public List<ListProjectResponse> listProject(@LoginUser UserEntity user){
        List<Project> projects = projectService.listProject(user.getId());
        return projectControllerMapper.toListProjectResponse(projects);
    }

    @PutMapping("/{id}")
    @Operation(summary = "프로젝트 수정" , description = "프로젝트 변경 시 사용합니다.")
    public ResponseEntity<?> updateProject(@LoginUser UserEntity user,@Parameter(description = "프로젝트 식별키") @PathVariable String id, @Valid @RequestBody UpdateProjectRequest updateProjectRequest){
        projectService.updateProject(user.getId(), id, updateProjectRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "프로젝트 삭제", description = "권한이 소유자인 경우만 삭제가 가능하도록 접근 제한 합니다")
    public ResponseEntity<?> deleteProject(@LoginUser UserEntity userEntity,@Parameter(description = "프로젝트 식별키") @PathVariable String id){
        projectService.deleteProject(userEntity.getId(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/function")
    @Operation(summary = "프로젝트 기능 목록", description = "프로젝트가 가진 기능에 대한 식별키 입니다. 사이드바로 위치. 캘린더, 보드 등등.. 식별키를 통해 프로젝트가 가진 기능을 조회")
    public ProjectFunctionResponse listProjectFunction(@Parameter(description = "프로젝트 식별키") @PathVariable String id){
        return projectService.listProjectFunction(id);
    }

    @GetMapping("/{projectId}/member")
    @Operation(summary = "프로젝트 참여 멤버 목록")
    public List<ProjectMemberResponse> listMember(@Parameter(description = "프로젝트 식별키")@PathVariable String projectId){
        return projectService.listMember(projectId);
    }

    @PostMapping("/{projectId}/member")
    @Operation(summary = "프로젝트 멤버 초대")
    public ResponseEntity<?> createMember(
            @Parameter(description = "프로젝트 식별키") @PathVariable String projectId,
            @Valid @RequestBody CreateMemberRequest createMemberRequest){
        projectService.createMember(projectId,createMemberRequest.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{projectId}/member")
    @Operation(summary = "프로젝트 멤버 삭제")
    public ResponseEntity<?> deleteMember(@LoginUser UserEntity userEntity,
                                          @Parameter(description = "프로젝트 식별키",required = true) @PathVariable String projectId,
                                          @Parameter(description = "프로젝트 참여 멤버 식별키",required = true)@RequestParam String projectMemberId){
        projectService.deleteMember(userEntity.getId(), projectMemberId, projectId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{projectId}/member")
    @Operation(summary = "프로젝트 멤버 수정", description = "프로젝트 권한 변경 시 사용")
    public ResponseEntity<?> updateMember(@LoginUser UserEntity userEntity,
                                          @Parameter(description = "프로젝트 식별키",required = true) @PathVariable String projectId,
                                          @Valid @RequestBody UpdateMemberRequest updateMemberRequest){
        projectService.updateMember(userEntity.getId(), updateMemberRequest, projectId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{projectId}/function")
    @Operation(summary = "프로젝트 기능 추가", description = "사이드바에 기능을 추가할 때 사용")
    public ResponseEntity<?> createProjectFunction(@Parameter(description = "프로젝트 식별키") @PathVariable String projectId,
                                                   @Valid @RequestBody CreateProjectFunctionRequest createProjectFunctionRequest,
                                                   @LoginUser UserEntity userEntity){
        projectService.createProjectFunction(userEntity.getId(), projectId, createProjectFunctionRequest);
        return ResponseEntity.ok().build();
    }

   @PutMapping("/{projectId}/function")
    public ResponseEntity<?> updateProjectFunction(@LoginUser UserEntity userEntity,
                                                   @Parameter(description = "프로젝트 식별키") @PathVariable String projectId,
                                                   @Valid @RequestBody UpdateProjectFunctionRequest updateProjectFunctionRequest){
        projectService.updateProjectFunction(userEntity.getId(), projectId, updateProjectFunctionRequest);
        return ResponseEntity.ok("ok");
   }


}
