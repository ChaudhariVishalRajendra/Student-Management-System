package com.springboot.spring_intro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseReqDto {

    @NotBlank(message = "Course Name is Required")
    private String courseName;

    @NotBlank(message = "Duration is Required")
    private String duration;

    @NotNull(message = "fees is Required")
    private Double fees;

    @NotBlank(message = "Instructor Name is Required")
    private String instructorName;

}
