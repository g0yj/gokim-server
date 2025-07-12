package com.lms.api.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScrapTableType {
    COMMUNITY("community");

    private final String tableName;
}
