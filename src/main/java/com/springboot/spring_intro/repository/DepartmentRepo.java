package com.springboot.spring_intro.repository;

import com.springboot.spring_intro.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepo extends JpaRepository<Department, Long> {

//    Optional<Department> findByDepartmentName(String departmentName);
}
