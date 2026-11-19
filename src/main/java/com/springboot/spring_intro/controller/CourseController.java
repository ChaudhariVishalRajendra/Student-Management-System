package com.springboot.spring_intro.controller;

import com.springboot.spring_intro.dto.CourseReqDto;
import com.springboot.spring_intro.dto.CourseResDto;
import com.springboot.spring_intro.entity.Course;
import com.springboot.spring_intro.payload.ApiResponse;
import com.springboot.spring_intro.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService service;

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResDto>> createCourse(@Valid @RequestBody CourseReqDto dto){
        CourseResDto res = service.createCourse(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        LocalDateTime.now(),
                        HttpStatus.CREATED.value(),
                        "Course Created Succesfully",
                        res
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResDto>>> getAllCourses(){
        return ResponseEntity.ok(
                new ApiResponse<>(
                true,
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Courses Fetched Succesfully",
                service.getAllCourses()
        ));
    }

    @GetMapping("/{id}")
    private ResponseEntity<ApiResponse<CourseResDto>> getCourseById(@PathVariable Long id){
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Courses Fetched Succesfully",
                        service.getCourseById(id)
        ));
    }

}
