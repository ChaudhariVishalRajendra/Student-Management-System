package com.springboot.spring_intro.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentReqDto {

    @NotBlank(message = "First Name is Required")
    @Size(min = 2, max = 30, message = "First Name Must be Between 2 to 30 Character")
    private String firstName;

    @NotBlank(message = "Last Name is Required")
    @Size(min = 2, max = 30, message = "Last Name Must be Between 2 to 30 Character")
    private String lastName;

    @NotBlank(message = "Email is Required")
    @Email(message = "Please enter a valid Email")
    private String email;

//    @NotBlank(message = "Course is Required")
//    private String course;

    @NotBlank(message = "Password is Required")
    @Size(min = 8, message = "Password Must contain at least 8 Character")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password Must Contain at least 8 Character, 1 uppercase letter, 1 lowercase letter, 1 number & 1 Special Character"
    )
    private String password;

    private AddressRequestDTO address;
    @NotNull(message = "Department Id is Required")
    private Long departmentId;

    @NotEmpty(message = "At least 1 Course is Required")
    private List<Long> courseId;

    private Long version;
//    private String profileImage;

}
