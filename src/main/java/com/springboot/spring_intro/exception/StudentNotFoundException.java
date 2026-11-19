package com.springboot.spring_intro.exception;

public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(String msg){
        super(msg);
    }

}
