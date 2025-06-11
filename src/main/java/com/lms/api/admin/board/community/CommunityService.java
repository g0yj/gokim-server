package com.lms.api.admin.board.community;


import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.File.dto.FileMeta;
import com.lms.api.admin.board.community.dto.CreateCommunityRequest;
import com.lms.api.common.entity.board.*;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.board.CommunityRepository;
import com.lms.api.common.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommunityService {
    private final S3FileStorageService s3FileStorageService;
    private final CommunityServiceMapper communityServiceMapper;
    private final CommunityRepository communityRepository;
    @Transactional
    public String createCommunity(String loginId, CreateCommunityRequest createCommunityRequest) {

        String ext = FileUtil.getFileExtension(createCommunityRequest.getFile().getOriginalFilename());
        if (!FileUtil.isAllowedImageExtension(ext)) {
            throw new ApiException(ApiErrorCode.UNSUPPORTED_FORMAT_ERROR);
        }

        FileMeta fileMeta = null;

        if (createCommunityRequest.getFile() != null ) {
            fileMeta = s3FileStorageService.upload(createCommunityRequest.getFile(), "board/community");
        }

        String id = "BC" +System.nanoTime();

        CommunityEntity communityEntity = CommunityEntity.builder()
                .id(id)
                .title(createCommunityRequest.getTitle())
                .description(createCommunityRequest.getDescription())
                .hasProject(false)
                .createdBy(loginId)
                .modifiedBy(loginId)
                .originalFileName(fileMeta != null ? fileMeta.getOriginalFileName() : null)
                .fileName(fileMeta != null ? fileMeta.getS3Key() : null)
                .build();

        communityRepository.save(communityEntity);
        return id;
    }
}



