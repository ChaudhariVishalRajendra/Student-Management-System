package com.springboot.spring_intro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResDto {

    private Long id;
    private String courseName;
    private String duration;
    private Double fees;
    private String instructorName;
}
