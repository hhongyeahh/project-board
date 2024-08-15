package com.project.projectboard.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/") // 홈화면
    public String root(){
        return "forward:/articles";
    }
}
