package com.springboot.spring_intro.repository.custom;

import com.springboot.spring_intro.entity.Student;

import java.util.List;

public interface StudentRepoCustom {
    List<Student> findStudentCustom();
    List<Student> findStudentByDepartment(String departmentName);
}
