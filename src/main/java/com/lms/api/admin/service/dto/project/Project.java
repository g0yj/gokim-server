package com.lms.api.admin.service.dto.project;

import com.lms.api.admin.service.dto.User;
import com.lms.api.admin.service.dto.project.task.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
public class Project {
    String id;
    String projectName;
    String ownerName;

    List<Task> tasks;
}
