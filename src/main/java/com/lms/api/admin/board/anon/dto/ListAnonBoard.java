package com.lms.api.admin.board.anon.dto;

import com.lms.api.common.dto.PageResponseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter@Setter@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListAnonBoard extends PageResponseData {

    String id;
    String title;
    LocalDate createDate;
    int fileCount;
    int view;


}
