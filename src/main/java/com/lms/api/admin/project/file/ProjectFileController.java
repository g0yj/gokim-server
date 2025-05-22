package com.lms.api.admin.project.file;

import com.lms.api.admin.auth.LoginUser;
import com.lms.api.admin.project.file.dto.CreateProjectFileRequest;
import com.lms.api.admin.project.file.dto.CreateProjectFile;
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
@RequestMapping("/project/file")
@Tag(name = "Project_file API", description = "프로젝트 기능 중 파일 관련 API입니다")
@RequiredArgsConstructor
@Slf4j
public class ProjectFileController {
    private final ProjectFileControllerMapper projectFileControllerMapper;
    private final ProjectFileService projectFileService;

    @PostMapping("/{projectFunctionId}")
    @Operation(summary ="프로젝트 파일 업로드" , description = "프로젝트 기능 중 파일 등록")
    public ResponseEntity<?> projectFileUpload(@LoginUser UserEntity userEntity, @Parameter(description = "프로젝트 기능 식별키" ) @PathVariable String projectFunctionId,
                                               @Valid @ModelAttribute CreateProjectFileRequest createProjectFileRequest){
        CreateProjectFile createProjectFile = projectFileControllerMapper.toProjectFile(userEntity.getId(), projectFunctionId, createProjectFileRequest);
        projectFileService.projectFileUpload(createProjectFile);
        return ResponseEntity.ok("ok");
    }

}
