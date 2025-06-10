package com.lms.api.admin.anon;


import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.File.dto.FileMeta;
import com.lms.api.admin.anon.dto.CreateAnonBoard;
import com.lms.api.common.entity.board.AnonBoardEntity;
import com.lms.api.common.entity.board.AnonBoardFileEntity;
import com.lms.api.common.repository.board.AnonBoardFileRepository;
import com.lms.api.common.repository.board.AnonBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnonBoardService {
    private final S3FileStorageService s3FileStorageService;
    private final AnonBoardRepository anonBoardRepository;
    private final AnonBoardFileRepository anonBoardFileRepository;
    private final AnonBoardServiceMapper anonBoardServiceMapper;

    @Transactional
    public String createAnonBoard(String loginId, CreateAnonBoard createAnonBoard) {
        String boardId = "BAN" + System.nanoTime();

        AnonBoardEntity anonBoardEntity = AnonBoardEntity.builder()
                .id(boardId)
                .title(createAnonBoard.getTitle())
                .content(createAnonBoard.getContent())
                .createdBy(createAnonBoard.getCreatedBy())
                .modifiedBy(createAnonBoard.getCreatedBy())
                .build();

        List<FileMeta> uploadFiles = s3FileStorageService.upload(createAnonBoard.getMultipartFiles(), "board/anon");

        for (FileMeta fileMeta : uploadFiles) {
            AnonBoardFileEntity fileEntity = AnonBoardFileEntity.builder()
                    .fileName(fileMeta.getS3Key())
                    .originalFileName(fileMeta.getOriginalFileName())
                    .createdBy(anonBoardEntity.getCreatedBy())
                    .modifiedBy(anonBoardEntity.getCreatedBy())
                    .build();

            anonBoardEntity.addFile(fileEntity); // ✅ 연관관계 설정, file.setAnonBoardEntity(this) 포함됨
        }

        anonBoardRepository.save(anonBoardEntity); // ✅ CascadeType.ALL에 의해 자식도 자동 저장
        return boardId;
    }

}



