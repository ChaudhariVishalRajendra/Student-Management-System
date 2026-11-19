package com.springboot.spring_intro.controller;

import com.springboot.spring_intro.dto.DepartmentReqDTO;
import com.springboot.spring_intro.dto.DepartmentResDTO;
import com.springboot.spring_intro.payload.ApiResponse;
import com.springboot.spring_intro.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentService departmentService;


    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResDTO>> createDepartment(@Valid @RequestBody DepartmentReqDTO dto){
        DepartmentResDTO res = departmentService.createDepartment(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        LocalDateTime.now(),
                        HttpStatus.CREATED.value(),
                        "Department created Succesfully",
                        res
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentResDTO>>> getAllDepartments(){
        List<DepartmentResDTO> res = departmentService.getAllDepartments();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Departments Fetched Succesfully",
                        res
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResDTO>> getDepartmentById(@PathVariable Long id){
        DepartmentResDTO res = departmentService.getDepartmentById(id);
        ApiResponse<DepartmentResDTO> apiResponse = new ApiResponse<>(
                true,
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Department Fetched Succesfully",
                res
        );
        return ResponseEntity.ok(apiResponse);
    };

}
