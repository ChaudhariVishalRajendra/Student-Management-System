package com.springboot.spring_intro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
//    private String course;

    private String departmentName;
    private AddressResponseDTO address;

    private List<CourseResDto> courses;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String createdBy;
    private String lastModifiedBy;

    private Long version;

    private String profileImage;

}
