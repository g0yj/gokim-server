package com.lms.api.admin.controller.project.task;


import com.lms.api.admin.controller.dto.project.task.CreateTaskRequest;
import com.lms.api.admin.service.project.task.TaskService;
import com.lms.api.common.dto.LoginUser;
import com.lms.api.common.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "이슈 만들기", description = "todo를 추가할 때 사용합니다.")
    public ResponseEntity<String> createTask(@LoginUser UserEntity user, @Valid @RequestBody CreateTaskRequest createTaskRequest){
        return ResponseEntity.ok(taskService.createTask(user, createTaskRequest));
    }
}
