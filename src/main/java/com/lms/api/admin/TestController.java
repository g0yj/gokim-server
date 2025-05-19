package com.lms.api.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {
    @GetMapping("/1")
    public String test1(){
        return "자동 배포 파일 내용 변경 확인 완료";
    }

    @GetMapping("/2")
    public String test2(){
        return "";
    }
}
