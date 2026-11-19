package com.springboot.spring_intro.repository;

import com.springboot.spring_intro.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepo extends JpaRepository<Course, Long> {

}
