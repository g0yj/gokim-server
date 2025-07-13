package com.lms.api.admin.user;

import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.auth.enums.LoginType;
import com.lms.api.admin.File.dto.FileMeta;
import com.lms.api.admin.user.dto.*;
import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.common.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final S3FileStorageService s3FileStorageService;
    private final UserRepository userRepository;

    @Transactional
    public CreateUserResponse createUser(CreateUser createUser) {

        FileMeta fileMeta = null;
        MultipartFile profileFile = createUser.getMultipartFile();

        // ✅ 파일이 있을 때만 확장자 검사 및 업로드
        if (profileFile != null && !profileFile.isEmpty()) {
            String ext = FileUtils.getFileExtension(profileFile.getOriginalFilename());
            if (!FileUtils.isAllowedImageExtension(ext)) {
                throw new ApiException(ApiErrorCode.UNSUPPORTED_FORMAT_ERROR);
            }
            fileMeta = s3FileStorageService.upload(profileFile, "user");
        }

        if (userRepository.existsById(createUser.getId())) {
            throw new ApiException(ApiErrorCode.LOGIN_SERVER_ERROR);
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


        UserEntity user = UserEntity.builder()
                .id(createUser.getId())
                .password(passwordEncoder.encode(createUser.getPassword()))
                .name(createUser.getName())
                .email(createUser.getEmail())
                .phone(createUser.getPhone())
                .fileName(fileMeta != null ? fileMeta.getS3Key() : null)
                .originalFileName(fileMeta != null ? fileMeta.getOriginalFileName() : null)
                .role(UserRole.USER)
                .loginType(LoginType.NORMAL)
                .createdBy(createUser.getId())
                .build();

        userRepository.save(user);
        return CreateUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    @Transactional
    public GetUser getUser(String userId) {
        log.debug("로그인 된 아이디 확인 : {}", userId);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        String profileImgUrl = s3FileStorageService.getUrl(userEntity.getFileName());

        return GetUser.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .userImgUrl(profileImgUrl) // key → full URL 변환
                .loginType(userEntity.getLoginType())
                .build();
    }

    @Transactional
    public void updateUser(String userId, UpdateUser updateUser) {

        String ext = FileUtils.getFileExtension(updateUser.getMultipartFile().getOriginalFilename());
        if (!FileUtils.isAllowedImageExtension(ext)) {
            throw new ApiException(ApiErrorCode.UNSUPPORTED_FORMAT_ERROR);
        }

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        userEntity.setName(updateUser.getName());
        userEntity.setPhone(updateUser.getPhone());
        userEntity.setEmail(updateUser.getEmail());
        userEntity.setModifiedBy(updateUser.getModifiedBy());

        // 비밀번호 수정 (있는 경우만)
        if (updateUser.getPassword() != null && !updateUser.getPassword().isEmpty()) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            userEntity.setPassword(bCryptPasswordEncoder.encode(updateUser.getPassword()));
        }

        // 프로필 파일 수정
        MultipartFile newFile = updateUser.getMultipartFile();
        if (newFile != null && !newFile.isEmpty()) {
            // 기존 파일 삭제
            String existingKey = userEntity.getFileName();
            if (existingKey != null && !existingKey.isBlank()) {
                s3FileStorageService.delete(existingKey);
            }

            // 새 파일 업로드
            FileMeta fileMeta = s3FileStorageService.upload(newFile, "user");

            userEntity.setOriginalFileName(fileMeta.getOriginalFileName());
            userEntity.setFileName(fileMeta.getS3Key());
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

    public void searchUser(String userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.USER_NOT_FOUND));
    }
}



