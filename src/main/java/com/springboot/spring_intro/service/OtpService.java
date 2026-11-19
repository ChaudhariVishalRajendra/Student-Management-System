package com.springboot.spring_intro.service;

public interface OtpService {
    void generateAndSendOtp(String email);
    void  verifyOtp(String email, String Otp);
}
