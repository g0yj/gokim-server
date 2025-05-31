package com.lms.api.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {
    @GetMapping("/1")
    public String test1(){
        return "자동 배포 테스트1";
    }


}
