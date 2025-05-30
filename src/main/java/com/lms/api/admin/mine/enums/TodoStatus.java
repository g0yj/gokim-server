package com.lms.api.admin.mine.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TodoStatus {
    READY("시작전"),
    IN_PROGRESS("진행중"),
    DONE("완료"),;

    private final String label;
}
