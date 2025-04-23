package com.lms.api.admin.controller.project.task;


import com.lms.api.admin.controller.dto.project.task.CreateTaskRequest;
import com.lms.api.admin.controller.dto.project.task.GetTaskResponse;
import com.lms.api.admin.controller.dto.project.task.ListTaskRequest;
import com.lms.api.admin.controller.dto.project.task.ListTaskResponse;
import com.lms.api.admin.service.dto.project.task.ListTask;
import com.lms.api.admin.service.project.task.TaskService;
import com.lms.api.common.dto.LoginUser;
import com.lms.api.common.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
@Tag(name = "Task API", description = "Task 관련 API입니다")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private final TaskService taskService;
    private final TaskControllerMapper taskControllerMapper;

    @PostMapping
    @Operation(summary = "Task 만들기", description = "task를 추가할 때 사용합니다.")
    public ResponseEntity<String> createTask(@LoginUser UserEntity user, @Valid @RequestBody CreateTaskRequest createTaskRequest){
        return ResponseEntity.ok(taskService.createTask(user, createTaskRequest));
    }

   @GetMapping
   @Operation(summary = "Task 목록" , description = "상태에 따라 그룹핑했습니다")
   public List<ListTaskResponse> listTask (@RequestBody ListTaskRequest listTaskRequest){
        List<ListTask> tasks = taskService.listTask(listTaskRequest);
        return taskControllerMapper.toListTaskResponse(tasks);
   }

   @GetMapping("/{id}")
   @Operation(summary = "Task 상세조회", description = "task 목록 중 하나 클릭 시 식별키(id)를 사용해 상세 페이지로 이동합니다")
    public GetTaskResponse getTask (@PathVariable String id) {
        return taskService.getTask(id);
   }

}

