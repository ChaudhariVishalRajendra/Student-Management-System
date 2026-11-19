package com.springboot.spring_intro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResDTO {

    private long id;
    private String departmentName;

    private List<StudentResDto> students;
}
