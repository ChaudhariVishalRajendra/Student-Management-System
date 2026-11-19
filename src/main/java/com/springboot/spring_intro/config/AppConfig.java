package com.springboot.spring_intro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public String appName(){
        return "Student Management System";
    }
}

