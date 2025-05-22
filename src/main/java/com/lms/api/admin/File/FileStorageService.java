package com.lms.api.admin.File;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * local 은 DB를 사용 하고 , dev는 S3을 사용하기 위해
 */
public interface FileStorageService {
    // 단일 파일 업로드 - 디렉토리 지정
    String upload(MultipartFile file, String subDir);

    // 단일 파일 업로드 - 기본 디렉토리 (오버로딩)
    String upload(MultipartFile file);

    // 다중 파일 업로드 - 디렉토리 지정
    Map<String, String> upload(List<MultipartFile> files, String subDir);

    // 다중 파일 업로드 - 기본 디렉토리 (오버로딩)
    Map<String, String> upload(List<MultipartFile> files);

    // 파일 삭제 - 디렉토리 + 파일명 기준
    void delete(String subDir, String fileName);

    // 파일 삭제 - 전체 key 기준
    void delete(String fullKey);

    // 파일 접근 URL 반환
    String getUrl(String fileName);
}
