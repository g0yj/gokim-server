package com.lms.api.admin.controller.dto.project;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListProjectResponse {
    String id;
    String projectName;

    String ownerName;

}
