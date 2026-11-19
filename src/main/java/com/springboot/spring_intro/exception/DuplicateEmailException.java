package com.springboot.spring_intro.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String msg) {
        super(msg);
    }

}
