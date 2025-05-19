package com.lms.api.admin.user;

import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.auth.enums.LoginType;
import com.lms.api.admin.user.dto.CreateUser;
import com.lms.api.admin.user.dto.CreateUserResponse;
import com.lms.api.admin.user.dto.GetUser;
import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final S3FileStorageService s3FileStorageService;
    private final UserRepository userRepository;

    @Transactional
    public CreateUserResponse createUser( CreateUser createUser){

        if(userRepository.existsById(createUser.getId())){
            throw new ApiException(ApiErrorCode.LOGIN_SERVER_ERROR);
        }
        String fileName = null;
        String originalFileName = null;

        if(createUser.getMultipartFile() != null && !createUser.getMultipartFile().isEmpty()){
            fileName = s3FileStorageService.upload(createUser.getMultipartFile());
            originalFileName = createUser.getMultipartFile().getOriginalFilename();
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        UserEntity user = UserEntity.builder()
                .id(createUser.getId())
                .password(passwordEncoder.encode(createUser.getPassword()))
                .name(createUser.getName())
                .email(createUser.getEmail())
                .phone(createUser.getPhone())
                .fileName(fileName)
                .originalFileName(originalFileName)
                .role(UserRole.USER)
                .loginType(LoginType.NORMAL)
                .build();
        userRepository.save(user);


        return CreateUserResponse.builder()
                .id(createUser.getId())
                .name(createUser.getName())
                .build();
    }

    @Transactional
    public GetUser getUser(String userId) {
        log.debug("로그인 된 아이디 확인 : {}" , userId);
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        return GetUser.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .userImgUrl(s3FileStorageService.getUrl(userEntity.getFileName()))
                .loginType(userEntity.getLoginType())
                .build();
    }

/*
    @Transactional
    public void updateProject(String modifiedBy,String id, UpdateProjectRequest updateProjectRequest){
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(()-> new ApiException(ApiErrorCode.PROJECT_NOT_FOUND));

        projectEntity.setModifiedBy(modifiedBy);
        projectEntity.setProjectName(updateProjectRequest.getProjectName());

        projectRepository.save(projectEntity);
    }

    @Transactional
    public void deleteProject(String userId, String projectId){
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.PROJECT_NOT_FOUND));

        ProjectMemberEntity projectMemberEntity = projectMemberRepository.findByProjectEntity_IdAndUserEntity_IdAndProjectRole(projectId, userId, ProjectRole.OWNER)
                .orElseThrow(() -> new ApiException(ApiErrorCode.ACCESS_DENIED));

        projectRepository.delete(projectEntity);
    }

    @Transactional
    public ProjectFunction projectFunction(String projectId){
        List<FunctionEntity> functionEntities = functionRepository.findByProjectEntity_IdOrderByFunctionSortAsc(projectId);
        List<ProjectFunction.Function> functions = functionEntities.stream()
                .map(projectServiceMapper::toFunction)
                .toList();

        return ProjectFunction.builder()
                .projectId(projectId)
                .functions(functions)
                .build();
    }*/
}



