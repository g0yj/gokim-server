package com.lms.api.admin.project.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Task 목록의 순서, 상태 변경 시 사용")
public class ChangeTaskRequest {
    @Schema(description = "task의 상태를 변경하거나 순서를 변경할 때마다 api 호출이 필요하며, 이때 사용합니다. 목록에 있는 모든 task의 sortOrder을 순서에 따라 변경하여 넘겨야합니다.")
    List<Change> changes;

    @Getter@Setter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Change {
        @Schema(description = "task 식별키")
        String taskId;
        @Schema(description = "task 정렬을 위한 순서")
        int sortOrder;
        @Schema(description = "task 상태 식별키")
        int taskStatusId;
    }

}
