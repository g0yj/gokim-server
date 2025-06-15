package com.lms.api.common.util;

import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileUtils {
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");
    /**
     * 파일 이름에서 확장자를 추출
     * @param filename ex. "image.png"
     * @return 확장자 (예: "png"), 없으면 빈 문자열 반환
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isBlank()) return "";

        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex != -1 && dotIndex < filename.length() - 1)
                ? filename.substring(dotIndex + 1)
                : "";
    }
    /**
     * 확장자가 이미지인지 여부
     */
    public static boolean isAllowedImageExtension(String ext) {
        if (ext == null || ext.isBlank()) return false;
        return ALLOWED_IMAGE_EXTENSIONS.contains(ext.toLowerCase());
    }


    /**
     * Multipart 파일 리스트가 허용된 최대 개수를 초과하는지 검증합니다.
     *
     * @param files 업로드된 MultipartFile 리스트 (null 허용)
     * @param max 허용 가능한 최대 파일 개수
     * @throws ApiException 파일 개수가 max를 초과하면 예외 발생
     */
    public static void validateSingleFileField(List<MultipartFile> files, int max) {
        if (files != null && files.size() > max) {
            throw new ApiException(ApiErrorCode.TOO_MANY_FILES);
        }
    }
    /**
     * 단일 MultipartFile을 List로 감싸거나 null을 처리합니다.
     *
     * @param file MultipartFile 객체 (nullable)
     * @return 파일이 null이면 null, 아니면 List로 감싼 결과 반환
     */
    public static List<MultipartFile> wrapToListIfNotNull(MultipartFile file) {
        return (file != null) ? List.of(file) : null;
    }

    /**
     * 파일이 null이어도 빈 리스트로 반환
     *
     * @param file MultipartFile 객체 (nullable)
     * @return 파일이 null이면 빈 리스트, 아니면 List로 감싼 결과
     */
    public static List<MultipartFile> wrapToListOrEmpty(MultipartFile file) {
        return (file != null) ? List.of(file) : List.of();
    }


}
