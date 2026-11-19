package com.springboot.spring_intro.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T>  { // Generic type T

    private Boolean success;
    private LocalDateTime timeStamp;
    private int status;
    private String message;
    private T data;

}
