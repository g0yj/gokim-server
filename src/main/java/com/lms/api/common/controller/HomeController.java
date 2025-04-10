package com.lms.api.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

  @RequestMapping
  public String home() {
    return "redirect:https://unialto.github.io/lms-api-doc/";
  }
}
