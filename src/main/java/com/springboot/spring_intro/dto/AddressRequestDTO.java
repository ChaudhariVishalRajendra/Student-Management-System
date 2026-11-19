package com.springboot.spring_intro.dto;

import lombok.Data;

@Data
public class AddressRequestDTO {
    private String city;
    private String state;
    private String country;
}
