package com.lms.api.admin.controller.project;

import com.lms.api.admin.controller.dto.project.*;
import com.lms.api.admin.service.dto.project.Function;
import com.lms.api.admin.service.dto.project.Project;
import com.lms.api.admin.service.project.ProjectService;
import com.lms.api.common.dto.LoginUser;
import com.lms.api.common.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
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
    @GetMapping("/{id}")
    @Operation(summary = "프로젝트 상세조회" , description = "프로젝트가 가진 여러 기능에 대한 정보를 포함하고 있습니다. 여러가지 기능은 식별키로 구분합니다")
    public GetProjectResponse getProject(@PathVariable String id){
        Function project = projectService.getProject(id);
        return projectControllerMapper.toGetProjectResponse(project);
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



}
