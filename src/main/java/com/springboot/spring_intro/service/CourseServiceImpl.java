package com.springboot.spring_intro.service;

import com.springboot.spring_intro.dto.CourseReqDto;
import com.springboot.spring_intro.dto.CourseResDto;
import com.springboot.spring_intro.entity.Course;
import com.springboot.spring_intro.exception.ResourceNotFoundException;
import com.springboot.spring_intro.repository.CourseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepo courseRepo;

    @Override
    public CourseResDto createCourse(CourseReqDto courseReqDto) {
        Course course = new Course();
        course.setCourseName(courseReqDto.getCourseName());
        course.setDuration(courseReqDto.getDuration());
        course.setFees(courseReqDto.getFees());
        course.setInstructorName(courseReqDto.getInstructorName());
        Course SavedCourse = courseRepo.save(course);
        return mapToResponseDto(SavedCourse);
    }

    @Override
    public List<CourseResDto> getAllCourses() {
        return courseRepo.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public CourseResDto getCourseById(Long id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not Found with ID: "+id));
        return mapToResponseDto(course);
    }

    private CourseResDto mapToResponseDto(Course course){
        CourseResDto dto = new CourseResDto();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setDuration(course.getDuration());
        dto.setFees(course.getFees());
        dto.setInstructorName(course.getInstructorName());
        return dto;
    };

}
