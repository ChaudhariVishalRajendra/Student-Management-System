package com.springboot.spring_intro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping("/")
    public String demo(){
        return "Spring Boot Started ";
    }

//    @PostMapping("/")
//    public String demo(){
//        return "Spring Boot Started ";
//    }

}

