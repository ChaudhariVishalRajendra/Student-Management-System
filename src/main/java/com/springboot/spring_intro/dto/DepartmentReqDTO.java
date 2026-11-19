package com.springboot.spring_intro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentReqDTO {

    @NotBlank(message = "Department Name is Required")
    @Size(max = 100, message = "Deparment Name can't exceed 100 Character")
    private String departmentName;
}
