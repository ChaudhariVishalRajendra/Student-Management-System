package com.springboot.spring_intro.service;

public interface EmailService {
    void sendOtpEmail(String toMail, String otp);
}
