package com.springboot.spring_intro.service;

import com.springboot.spring_intro.dto.CourseResDto;
import com.springboot.spring_intro.dto.DepartmentReqDTO;
import com.springboot.spring_intro.dto.DepartmentResDTO;
import com.springboot.spring_intro.dto.StudentResDto;
import com.springboot.spring_intro.entity.Department;
import com.springboot.spring_intro.repository.DepartmentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepo departmentRepo;

    @Override
    public DepartmentResDTO createDepartment(DepartmentReqDTO dto) {
        Department department = new Department();
        department.setDepartmentName(dto.getDepartmentName());
        Department savedDepartment = departmentRepo.save(department);
        return mapToResponseDto(savedDepartment);
    }

    @Override
    public List<DepartmentResDTO> getAllDepartments(){
        return departmentRepo.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public DepartmentResDTO getDepartmentById(Long id) {
        Department department = departmentRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Department Not Found With Id: "+id));
        return mapToResponseDto(department);
    }

    private DepartmentResDTO mapToResponseDto(Department department){
        DepartmentResDTO dto = new DepartmentResDTO();
        dto.setId(department.getId());
        dto.setDepartmentName(department.getDepartmentName());
        List<StudentResDto> student = department.getStudents()
                .stream()
                .map(std -> {
                    StudentResDto studentDto = new StudentResDto();
                    studentDto.setId(std.getId());
                    studentDto.setFirstName(std.getFirstName());
                    studentDto.setLastName(std.getLastName());
                    studentDto.setEmail(std.getEmail());

                    studentDto.setCourses(std.getCourses()
                            .stream()
                            .map(course->{
                                    CourseResDto dto1 = new CourseResDto();
                                    dto1.setId(course.getId());
                                    dto1.setCourseName(course.getCourseName());
                                    return dto1;
                            }).toList()
                    );
                    return studentDto;
                }).toList();
        dto.setStudents(student);
        return dto;
    }

}
