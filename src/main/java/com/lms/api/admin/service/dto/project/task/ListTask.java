package com.lms.api.admin.service.dto.project.task;

import lombok.Getter;

import java.util.List;

@Getter
public class ListTask {
    Long id;
    String title;
    int sortOrder;

    String projectId;
    String taskStatusId;


}
