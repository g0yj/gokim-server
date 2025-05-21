package com.lms.api.admin.user.dto;

import com.lms.api.admin.auth.enums.LoginType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@Setter@Builder
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "GetUser", description = "회원 상세 조회 , 회원 정보 수정에 사용 됩니다")
public class GetUser {
    @Schema(description = "회원 아이디로 변경이 불가능 하도록 조건 추가 필요 (비활성화 등)")
    String id;
    @Schema(description = "회원 이름")
    String name;
    @Schema(description = "회원 이메일로 loginType에 따라 비활성화가 필요")
    String email;
    @Schema(description = "회원 전화번호")
    String phone;
    @Schema(description = "회원 이미지로 src에 들어가는 주소")
    String userImgUrl;
    @Schema(description = "회원가입 경로를 나타냅니다. 소셜 로그인일 경우 이메일 변경이 불가능 하도록 하기 위한 조건으로 사용")
    LoginType loginType;
}
