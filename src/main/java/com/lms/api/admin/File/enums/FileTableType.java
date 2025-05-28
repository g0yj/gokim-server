package com.lms.api.admin.File.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
public enum FileTableType {
    PROJECT("project_file"),
    TASK("task_file"),
    NOTICE("notice_file");

    private final String tableName;
}

