package com.lms.api.admin.service.dto.project;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Function {
    String projectId;
    // 기능
    List<String> taskId;
}
