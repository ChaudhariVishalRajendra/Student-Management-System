package com.springboot.spring_intro.service;

import com.springboot.spring_intro.dto.CourseReqDto;
import com.springboot.spring_intro.dto.CourseResDto;

import java.util.List;

public interface CourseService {
    CourseResDto createCourse(CourseReqDto courseReqDto);
    List<CourseResDto> getAllCourses();
    CourseResDto getCourseById(Long id);
}
