package com.lms.api.admin.project.task;


import com.lms.api.admin.project.task.dto.*;
import com.lms.api.admin.project.task.dto.ChangeTask;
import com.lms.api.admin.project.task.dto.GetTask;
import com.lms.api.admin.project.task.dto.ListTask;
import com.lms.api.admin.project.task.dto.UpdateTask;
import com.lms.api.admin.auth.LoginUser;
import com.lms.api.common.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @PostMapping("/taskstatus")
    @Operation(summary = "TaskStatus 만들기", description = "진행, 완료 등 진행 상태를 만드는 api")
    public List<CreateTaskStatusResponse> createTaskStatus(@LoginUser UserEntity userEntity, @Valid @RequestBody CreateTaskStatusRequest createTaskStatusRequest){
        return taskService.createTaskStatus(userEntity.getId(),createTaskStatusRequest);
    }

    @GetMapping("/{projectFunctionId}/taskstatus")
    @Operation(summary = "TaskStatus 목록")
    public List<ListTaskStatusResponse> listTaskStatus(@Parameter(description = "기능 식별키(projectFunctionId)")@PathVariable String projectFunctionId){
        List<ListTask.TaskStatus> taskStatuses = taskService.listTaskStatus(projectFunctionId);
        return taskControllerMapper.toListTaskStatusResponse(taskStatuses);
    }

    @PutMapping("/taskstatus/{taskStatusId}")
    @Operation(summary = "TaskStatus 수정")
    public ResponseEntity<?> updateTaskStatus(@Parameter(description = "상태 식별키", required = true) @PathVariable Long taskStatusId,
                                              @Valid @RequestBody UpdateTaskStatusRequest updateTaskStatusRequest){
        taskService.updateTaskStatus(taskStatusId, updateTaskStatusRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/taskstatus/{taskStatusId}")
    @Operation(summary = "TaskStatus 삭제")
    public ResponseEntity<?> deleteTaskStatus(@Parameter(description = "상태 식별키", required = true) @PathVariable Long taskStatusId, @LoginUser UserEntity userEntity){
        taskService.deleteTaskStatus(taskStatusId);
        return ResponseEntity.ok().build();
    }
    @PostMapping
    @Operation(summary = "Task 만들기", description = "task를 추가할 때 사용합니다.")
    public ResponseEntity<String> createTask(@LoginUser UserEntity user, @Valid @RequestBody CreateTaskRequest createTaskRequest){
        return ResponseEntity.ok(taskService.createTask(user.getId(), createTaskRequest));
    }

   @GetMapping
   @Operation(summary = "Task 목록" , description = "상태에 따라 그룹핑했습니다")
   public List<ListTaskResponse> listTask (@RequestBody ListTaskRequest listTaskRequest){
        List<ListTask> tasks = taskService.listTask(listTaskRequest);
        return taskControllerMapper.toListTaskResponse(tasks);
   }
    @PutMapping("/change")
    @Operation(summary = "Task 목록 수정", description = " 상세 조회에서 수정 시 사용 , " +
                                                        "task의 상태를 변경하거나 순서를 변경할 때마다 api 호출이 필요하며, 이때 사용합니다. 목록에 있는 모든 task의 sortOrder을 순서에 따라 변경하여 넘겨야합니다.")
    public ResponseEntity<?> changeTask (@LoginUser UserEntity user, @RequestBody ChangeTaskRequest changeTaskRequest){
        ChangeTask changeTask = taskControllerMapper.toChangeTask(user.getId(), changeTaskRequest);
        taskService.changeTask(changeTask);
        return ResponseEntity.ok().build();
    }

   @GetMapping("/{id}")
   @Operation(summary = "Task 상세조회", description = "task 목록 중 하나 클릭 시 식별키(id)를 사용해 상세 페이지로 이동합니다")
    public GetTaskResponse getTask (@Parameter(description = "task 식별키") @PathVariable String id) {
        GetTask task = taskService.getTask(id);
        return taskControllerMapper.toGetTaskResponse(task);
   }

   @PutMapping("/{id}")
   @Operation(summary = "Task 수정", description = " 상세 조회에서 수정 시 사용")
    public ResponseEntity<?> updateTask (@LoginUser UserEntity user,
                                         @Parameter(description = "task 식별키") @PathVariable String id,
                                         UpdateTaskRequest updateTaskRequest){
       UpdateTask updateTask = taskControllerMapper.toUpdateTask(user.getId(), id, updateTaskRequest);
       taskService.updateTask(updateTask);
        return ResponseEntity.ok().build();
   }

   @DeleteMapping("/{id}")
   @Operation(summary = "Task 삭제")
   public ResponseEntity<?> deleteTask(@LoginUser UserEntity userEntity,@Parameter(description = "task 식별키") @PathVariable String id){
        taskService.deleteTask(userEntity.getId(), id);
        return ResponseEntity.ok().build();
   }

   @GetMapping("/{id}/subtask")
   @Operation(summary = "하위 항목 목록", description = "task 목록에서 식별키로 상세 페이지가 조회됩니다. 상세 페이지 안에 하위 업무 목록입니다")
   public List<ListSubTaskResponse> listSubTask(@Parameter(description = "task 식별키") @PathVariable String id){
        return taskService.listSubTask(id);
   }
   @PostMapping("/{subTaskId}")
   @Operation(summary = "하위 항목 등록")
   public ResponseEntity<?> createSubTask(@LoginUser UserEntity userEntity,
                                          @Parameter(description = "하위 항목 식별키") @PathVariable String subTaskId,
                                          @Valid @RequestBody CreateSubTaskRequest createSubTaskRequest){
        taskService.createSubTask(userEntity.getId(), subTaskId, createSubTaskRequest);

        return ResponseEntity.ok().build();
   }
   @PutMapping("/{subTaskId}")
   @Operation(summary = "하위 항목 수정", description = "요약 뿐 아니라, 담당자와 상태가 변경될 때도 사용됩니다.")
   public ResponseEntity<?> updateSubTask(@LoginUser UserEntity userEntity,
                                          @Parameter(description = "하위 항목 식별키") @PathVariable long subTaskId,
                                          @Valid @RequestBody UpdateSubTaskRequest updateSubTaskRequest
                                          ){
        taskService.updateSubTask(userEntity.getId(), subTaskId, updateSubTaskRequest);
        return ResponseEntity.ok().build();
   }

   @PostMapping("/{id}/comment")
   @Operation(summary = "댓글 등록")
   public ResponseEntity<?> createComment(@LoginUser UserEntity userEntity , @Parameter(description = "task식별키", required = true) @PathVariable String id,
                                          @Valid @RequestBody CreateCommentRequest createCommentRequest){
        taskService.createComment(userEntity.getId(), id, createCommentRequest);
        return ResponseEntity.ok().build();
   }
    @PutMapping("/{taskCommentId}")
    @Operation(summary = "댓글 수정")
    public ResponseEntity<?> updateComment(@LoginUser UserEntity userEntity,
                                           @Parameter(description = "댓글 식별키") @PathVariable Long taskCommentId,
                                           @Valid @RequestBody UpdateCommentRequest updateCommentRequest){
        taskService.updateComment(userEntity.getId(), taskCommentId, updateCommentRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskCommentId}")
    @Operation(summary = "댓글 삭제")
    public ResponseEntity<?> deleteComment(@Parameter(description = "댓글 식별키") @PathVariable Long taskCommentId){
        taskService.deleteComment(taskCommentId);
        return ResponseEntity.ok().build();
    }
}

