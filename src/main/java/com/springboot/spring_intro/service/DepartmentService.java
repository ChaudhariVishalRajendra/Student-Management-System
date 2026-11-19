package com.springboot.spring_intro.service;

import com.springboot.spring_intro.dto.DepartmentReqDTO;
import com.springboot.spring_intro.dto.DepartmentResDTO;

import java.util.List;


public interface DepartmentService {
    DepartmentResDTO createDepartment(DepartmentReqDTO dto);
    List<DepartmentResDTO> getAllDepartments();
    DepartmentResDTO getDepartmentById(Long Id);
}
