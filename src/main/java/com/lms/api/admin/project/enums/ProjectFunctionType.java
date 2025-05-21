package com.lms.api.admin.project.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Schema(description = "우측에 어떤 컴포넌트를 호출할지 판단 시 사용 <br>" +
        "TASK : 보드 관련 기능 <br>" +
        "FILE : 첨부 파일 관련 기능 <br>" +
        "BOARD : 게시판(기본crud) 기능 <br>" +
        "CALENDAR : 캘린더 기능 <br>")
public enum ProjectFunctionType {
    TASK("보드"),
    FILE("첨부 파일"),
    BOARD("게시판"),
    CALENDAR("캘린더"),
    PAGE("빈페이지")
    ;

    String label;
}
