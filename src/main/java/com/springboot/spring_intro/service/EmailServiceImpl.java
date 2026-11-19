package com.springboot.spring_intro.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String toMail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toMail);
        message.setSubject(
                "Student Management System - OTP Verification"
        );
        message.setText(
                "Your OTP is: "+otp+
                        "\n\n This OTP is Valid for 5 Minutes"
        );
        mailSender.send(message);
    }
}
