package com.lms.api.common.util;

import java.util.List;

public class FileUtil {
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

}
