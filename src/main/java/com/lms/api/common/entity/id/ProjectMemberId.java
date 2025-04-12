package com.lms.api.common.entity.id;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * 복합키 사용
 * - 복합키 클래스 만들기
 * - 엔티티에 @IdClass 설정
 * - @Repository의 반환타입을 ProjectMemberId 사용.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectMemberId implements Serializable {
    String projectMemberId;
    Long projectId;
}
