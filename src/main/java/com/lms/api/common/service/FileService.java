package com.lms.api.common.service;

import static com.lms.api.common.exception.LmsErrorCode.FILE_SIZE_EXCEEDED;
import static com.lms.api.common.exception.LmsErrorCode.FILE_UPLOAD_ERROR;

import com.lms.api.common.exception.LmsException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileService {

  @Value("${lms.file.host}")
  private String host;

  @Value("${lms.file.upload-dir}")
  private String uploadDir;

  @Value("${lms.file.max-file-size}")
  private String maxFileSizeStr;

  private long getMaxFileSize() {
    return DataSize.parse(maxFileSizeStr).toBytes(); // 파일 크기 문자열을 바이트 단위로 변환
  }

  public Map<String, String> upload(List<MultipartFile> files) {
    Map<String, String> fileNames = new HashMap<>();

    if (files == null) return fileNames;

    files.stream()
        .filter(file -> file != null && !file.isEmpty())
        .forEach(file -> {
          // 파일 크기 체크
          if (file.getSize() > getMaxFileSize()) {
            throw new LmsException(FILE_SIZE_EXCEEDED, maxFileSizeStr, file.getOriginalFilename());
          }

          String fileName = upload(file);

          if (fileName != null) fileNames.put(file.getOriginalFilename(), fileName);
        });

    return fileNames;
  }

  public String upload(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }

    // 파일 크기 체크
    if (file.getSize() > getMaxFileSize()) {
      throw new LmsException(FILE_SIZE_EXCEEDED, maxFileSizeStr, file.getOriginalFilename());
    }

    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

    // 업로드 디렉토리 존재 여부 체크 및 생성
    File dir = new File(uploadDir);
    if (!dir.exists()) {
      if (!dir.mkdirs()) {  // 디렉토리 생성 실패 시 예외 발생
        throw new LmsException(FILE_UPLOAD_ERROR, "Failed to create upload directory");
      }
    }

    try {
      file.transferTo(new File(uploadDir + fileName));

      return fileName;
    } catch (IOException e) {
      throw new LmsException(FILE_UPLOAD_ERROR, e);
    }
  }

  public String getUrl(String fileName, String originalFileName) {
    if (fileName == null || originalFileName == null) {
      return null;
    }

    return host + "/api/file/download/" + fileName + "/" + originalFileName;
  }

  public File get(String fileName) {
    return new File(uploadDir + fileName);
  }
}
