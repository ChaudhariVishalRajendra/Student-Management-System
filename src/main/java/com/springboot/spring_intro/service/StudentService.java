package com.springboot.spring_intro.service;

import com.springboot.spring_intro.dto.StudentProjectionDto;
import com.springboot.spring_intro.dto.StudentReqDto;
import com.springboot.spring_intro.dto.StudentResDto;
import com.springboot.spring_intro.entity.Student;
import com.springboot.spring_intro.projection.StudentProjection;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentService {
    StudentResDto saveStudent(StudentReqDto dto);
    List<StudentResDto> getAllStudents();
    StudentResDto getStudentById(Long id);
    void deleteStudent(Long id);
    StudentResDto updateStudent(Long id, StudentReqDto dto);
    StudentResDto patchStudent(Long id, StudentReqDto dto);

    //
    StudentResDto getStudentByEmail(String email);
    Long getStudentCountByCourse(String courseName);
    void deleteStudentByEmail(String email);
    List<StudentResDto> getStudentByFirstNameAndCourse(String firstName, String course);
    List<StudentResDto> findByFirstNameContaining(String keyword);
    StudentResDto getStudentByEmailNative(String email);
    List<StudentResDto> getAllStudentSortedByFirstName();

    //Pagination
    Page<StudentResDto> getStudent(Pageable pageable);

    // Dynamic Projection
    List<StudentProjectionDto> getStudentProjections();

    //JPA Specification
    public List<StudentResDto> searchStudent(String firstName, String lastName, String email, String courses);

    // Custom
    List<StudentResDto> getAllStudentCustom();
    List<StudentResDto> getStudentByDepartmentCustom(String departmentName);

    StudentResDto uploadProfileImage(Long id, MultipartFile file);
    Resource downloadProfileImage(Long id);
}
