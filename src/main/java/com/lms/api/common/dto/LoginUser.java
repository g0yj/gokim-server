package com.lms.api.common.dto;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER) // 파라미터에만 사용 가능하도록 지정
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지되어 ArgumentResolver에서 감지 가능
@Documented // Javadoc에 표시됨 (선택)
public @interface LoginUser { // 어노테이션은 @interface로 선언
}
