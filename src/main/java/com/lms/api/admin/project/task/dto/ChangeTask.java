package com.lms.api.admin.project.task.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeTask {

    List<Change> changes;

    String modifiedBy;

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Change {
        String taskId;
        int sortOrder;
        int taskStatusId;
    }
}
