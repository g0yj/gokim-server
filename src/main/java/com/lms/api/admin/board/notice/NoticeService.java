package com.lms.api.admin.board.notice;


import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.board.dto.CreateNotice;
import com.lms.api.admin.project.file.dto.FileMeta;
import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.board.NoticeEntity;
import com.lms.api.common.entity.board.NoticeFileEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.common.repository.board.NoticeFileRepository;
import com.lms.api.common.repository.board.NoticeRepository;
import com.mysql.cj.protocol.x.Notice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {
    private final JpaConfig jpaConfig;
    private final S3FileStorageService s3FileStorageService;
    private final NoticeServiceMapper noticeServiceMapper;
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final UserRepository userRepository;

    @Transactional
    public String createNotice(String loginId, CreateNotice createNotice) {

        UserEntity admin1 = userRepository.findByIdAndRole(createNotice.getCreatedBy(), UserRole.ADMIN)
                .orElseThrow(() -> new ApiException(ApiErrorCode.ACCESS_DENIED));

        String noticeId = "BN" + System.nanoTime();

        NoticeEntity noticeEntity = NoticeEntity.builder()
                .id(noticeId)
                .title(createNotice.getTitle())
                .content(createNotice.getContent())
                .pinned(createNotice.isPinned())
                .createdBy(loginId)
                .build();

        List<FileMeta> uploadedFiles = s3FileStorageService.upload(createNotice.getMultipartFiles(), "board/notice");

        List<NoticeFileEntity> fileEntities = uploadedFiles.stream()
                .map(fileMeta -> NoticeFileEntity.builder()
                            .fileName(fileMeta.getS3Key())
                            .originalFileName(fileMeta.getOriginalFileName())
                            .noticeEntity(noticeEntity)
                            .createdBy(createNotice.getCreatedBy())
                            .build())
                .collect(Collectors.toList());

        noticeEntity.setNoticeFileEntities(fileEntities);
        noticeRepository.save(noticeEntity);
        return noticeId;
    }
}



