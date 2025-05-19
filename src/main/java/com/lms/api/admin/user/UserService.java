package com.lms.api.admin.user;

import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.auth.enums.LoginType;
import com.lms.api.admin.project.dto.ProjectFunction;
import com.lms.api.admin.project.enums.ProjectRole;
import com.lms.api.admin.user.dto.*;
import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.FunctionEntity;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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

    @Transactional
    public void updateUser(String userId, UpdateUser updateUser){
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        userEntity.setName(updateUser.getName());
        userEntity.setPhone(updateUser.getPhone());
        userEntity.setEmail(updateUser.getEmail());
        userEntity.setModifiedBy(updateUser.getModifiedBy());

        // 비밀번호가 있을 경우만 수정
        String newPwd = null;
        if(updateUser.getPassword() != null && !updateUser.getPassword().isEmpty()){
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            newPwd = bCryptPasswordEncoder.encode(updateUser.getPassword());
            userEntity.setPassword(newPwd);
        }
        // multipart가 들어오면 수정 + userEntity의 fileName를 삭제해야함.
        if(updateUser.getMultipartFile() != null && !updateUser.getMultipartFile().isEmpty()){
            String deleteFile = s3FileStorageService.getUrl(userEntity.getFileName());
            s3FileStorageService.delete(deleteFile);
            String fileName = s3FileStorageService.upload(updateUser.getMultipartFile());
            userEntity.setOriginalFileName(updateUser.getMultipartFile().getOriginalFilename());
            userEntity.setFileName(fileName);
        }
        userRepository.save(userEntity);
    }


    @Transactional
    public void deleteUser(String userId){
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        //S3에서 삭제
        s3FileStorageService.delete(userEntity.getFileName());
        userRepository.delete(userEntity);
    }

}



