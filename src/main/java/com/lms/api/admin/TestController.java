package com.lms.api.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
    @GetMapping("/1")
    public String test1(){
        return "자동 배포 테스트1";
    }

    @GetMapping("/2")
    public String test2(){
        return "자동 배포 테스트2";
    }
    @GetMapping("/3")
    public String test3(){
        return "자동 배포 테스트3";
    }
    @GetMapping("/4")
    public String test4(){
        return "자동 배포 테스트4";
    }

    @GetMapping("/5")
    public String test5(){
        return "자동 배포 테스트5";
    }

}
